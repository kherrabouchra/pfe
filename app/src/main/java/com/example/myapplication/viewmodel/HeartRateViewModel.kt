package com.example.myapplication.viewmodel

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.HeartRateResult
import com.example.myapplication.data.MeasurementState
import com.example.myapplication.data.SignalQuality
import com.example.myapplication.data.SpO2Result
import com.example.myapplication.data.VitalSignsResult
import com.example.myapplication.utils.PpgSignalProcessor
import com.example.myapplication.utils.SignalProcessingUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class HeartRateViewModel : ViewModel() {
    
    private val TAG = "HeartRateViewModel"
    
    // Data buffers for PPG signal processing
    private val redValues = mutableListOf<Double>()
    private val greenValues = mutableListOf<Double>()
    private val blueValues = mutableListOf<Double>()
    private val frameToFrameVariation = mutableListOf<Double>()
    
    // Configuration parameters
    private val measurementDuration = 30000L
    private val maxMotionThreshold = 15.0  // Increased to be more tolerant of motion during walking
    private val minValidHeartRate = 40  // Lowered to detect bradycardia
    private val maxValidHeartRate = 180  // Increased to allow higher heart rates during activity
    private val minSignalAmplitude = 1.5  // Reduced for better sensitivity during movement
    private val signalQualityThreshold = 0.5  // Reduced for more tolerance during activity
    
    // State flows
    private val _measurementState = MutableStateFlow<MeasurementState>(MeasurementState.Idle)
    val measurementState: StateFlow<MeasurementState> = _measurementState.asStateFlow()
    
    private val _heartRateResult = MutableStateFlow<HeartRateResult?>(null)
    val heartRateResult: StateFlow<HeartRateResult?> = _heartRateResult.asStateFlow()
    
    private val _spO2Result = MutableStateFlow<SpO2Result?>(null)
    val spO2Result: StateFlow<SpO2Result?> = _spO2Result.asStateFlow()
    
    private val _vitalSignsResult = MutableStateFlow<VitalSignsResult?>(null)
    val vitalSignsResult: StateFlow<VitalSignsResult?> = _vitalSignsResult.asStateFlow()
    
    private val _isFingerDetected = MutableStateFlow(false)
    val isFingerDetected: StateFlow<Boolean> = _isFingerDetected.asStateFlow()
    
    private val _signalQuality = MutableStateFlow(SignalQuality.UNKNOWN)
    val signalQuality: StateFlow<SignalQuality> = _signalQuality.asStateFlow()
    
    private val _progress = MutableStateFlow(0)
    val progress: StateFlow<Int> = _progress.asStateFlow()
    
    private val _ppgSignalData = MutableStateFlow<List<Double>>(emptyList())
    val ppgSignalData: StateFlow<List<Double>> = _ppgSignalData.asStateFlow()
    
    private val _lastPulseTimestamp = MutableStateFlow<Long>(0)
    val lastPulseTimestamp: StateFlow<Long> = _lastPulseTimestamp.asStateFlow()
    
    private val _currentBpmEstimate = MutableStateFlow(0)
    val currentBpmEstimate: StateFlow<Int> = _currentBpmEstimate.asStateFlow()
    
    fun validateFingerPresence() {
        if (redValues.size < 10) return
        
        val recentValues = 10
        val recentRedValues = redValues.takeLast(recentValues)
        val recentGreenValues = greenValues.takeLast(recentValues)
        val recentBlueValues = blueValues.takeLast(recentValues)
        
        val avgRedIntensity = recentRedValues.average()
        val avgGreenIntensity = recentGreenValues.average()
        val avgBlueIntensity = recentBlueValues.average()
        val signalQualityScore = PpgSignalProcessor.calculateSignalQuality(recentRedValues)
        
        // Use the improved finger detection algorithm from PpgSignalProcessor
        val isFingerDetectedByAlgorithm = PpgSignalProcessor.isFingerDetected(
            avgRedIntensity, avgGreenIntensity, avgBlueIntensity
        )
        
        _isFingerDetected.value = isFingerDetectedByAlgorithm && signalQualityScore >= 0.4
        
        // Update current BPM estimate when finger is detected
        if (_isFingerDetected.value) {
            val currentEstimate = getCurrentBpmEstimate()
            if (currentEstimate > 0) {
                _currentBpmEstimate.value = currentEstimate
            }
        }
    }
    
    fun determineConfidenceLevel(): String {
        val hasExcessiveMotion = frameToFrameVariation.average() > maxMotionThreshold
        
        val goodSignalRatio = redValues.count { it > 50 }.toDouble() / redValues.size.toDouble()
        
        val filteredRedValues = SignalProcessingUtils.enhancedBandpassFilter(redValues)
        val peaks = SignalProcessingUtils.detectPeaks(filteredRedValues)
        
        val peakConsistency = if (peaks.size >= 3) {
            val intervals = mutableListOf<Int>()
            for (i in 1 until peaks.size) {
                intervals.add(peaks[i] - peaks[i-1])
            }
            
            val meanInterval = intervals.average()
            
            if (meanInterval > 0) {
                val stdDev = sqrt(intervals.map { (it - meanInterval).pow(2) }.average())
                1.0 - (stdDev / meanInterval).coerceIn(0.0, 0.5) * 2.0
            } else {
                0.0
            }
        } else {
            0.0
        }
        
        val signalStability = if (frameToFrameVariation.size >= 10) {
            val normalizedVariation = frameToFrameVariation.average() / maxMotionThreshold
            (1.0 - normalizedVariation.coerceIn(0.0, 1.0))
        } else {
            0.5
        }
        
        val signalQualityScore = PpgSignalProcessor.calculateSignalQuality(redValues)
        
        val combinedQuality = (goodSignalRatio * 0.25) + 
                             (signalQualityScore * 0.35) + 
                             (peakConsistency * 0.2) + 
                             (signalStability * 0.2)
        
        return when {
            hasExcessiveMotion -> "Low"
            combinedQuality >= 0.8 -> "High"
            combinedQuality >= 0.6 -> "Medium"
            else -> "Low"
        }
    }
    
    fun getCurrentBpmEstimate(): Int {
        if (redValues.size < 30) return 0
        
        val recentData = redValues.takeLast(90)
        val filteredData = SignalProcessingUtils.enhancedBandpassFilter(recentData)
        val peaks = SignalProcessingUtils.detectPeaks(filteredData)
        
        return if (peaks.size >= 2) {
            val intervals = mutableListOf<Int>()
            for (i in 1 until peaks.size) {
                intervals.add(peaks[i] - peaks[i-1])
            }
            
            // Filter out unrealistic intervals
            val validIntervals = intervals.filter { interval ->
                val instantBpm = (60.0 * 30.0 / interval).toInt() // Convert to BPM (30Hz sampling)
                instantBpm in 40..180 // Expanded range to accept higher heart rates during activity
            }
            
            if (validIntervals.isNotEmpty()) {
                val avgInterval = validIntervals.average()
                val bpm = (60.0 * 30.0 / avgInterval).toInt()
                bpm // No need to coerce as we already filtered intervals
            } else {
                0 // Return 0 if no valid intervals found
            }
        } else {
            0
        }
    }
    
    private fun calculateVitalSigns() {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
                val timestamp = dateFormat.format(Date())
                
                val preprocessedRedValues = PpgSignalProcessor.preprocessPpg(redValues)
                val preprocessedGreenValues = PpgSignalProcessor.preprocessPpg(greenValues)
                
                val filteredRedValues = SignalProcessingUtils.enhancedBandpassFilter(preprocessedRedValues)
                val filteredGreenValues = SignalProcessingUtils.enhancedBandpassFilter(preprocessedGreenValues)
                
                val samplingRate = 30.0
                
                // Calculate heart rate with multiple methods and use weighted average
                val hrMethod1 = PpgSignalProcessor.calculateHr(filteredRedValues, samplingRate).toInt()
                val hrMethod2 = getCurrentBpmEstimate()
                
                // Use weighted average of both methods if both are valid
                val calculatedHeartRate = when {
                    hrMethod1 >= minValidHeartRate && hrMethod2 >= minValidHeartRate -> {
                        val weight1 = 0.7  // Give more weight to PpgSignalProcessor
                        val weight2 = 0.3
                        ((hrMethod1 * weight1) + (hrMethod2 * weight2)).toInt()
                    }
                    hrMethod1 >= minValidHeartRate -> hrMethod1
                    hrMethod2 >= minValidHeartRate -> hrMethod2
                    else -> 0 // Invalid reading if both methods fail
                }
                
                val validatedHeartRate = calculatedHeartRate.coerceIn(minValidHeartRate, maxValidHeartRate)
                
                val heartRateStatus = when {
                    validatedHeartRate < 60 -> "Low"
                    validatedHeartRate > 100 -> "High"
                    else -> "Normal"
                }
                
                val oxygenSaturation = PpgSignalProcessor.calculateSpO2(filteredRedValues, filteredGreenValues).toInt().coerceIn(90, 100)
                
                val oxygenStatus = when {
                    oxygenSaturation < 95 -> "Low"
                    else -> "Normal"
                }
                
                val confidenceLevel = determineConfidenceLevel()
                
                val heartRateResult = HeartRateResult(validatedHeartRate, heartRateStatus, timestamp, confidenceLevel)
                val spO2Result = SpO2Result(oxygenSaturation, oxygenStatus, timestamp, confidenceLevel)
                val vitalSignsResult = VitalSignsResult(
                    validatedHeartRate,
                    oxygenSaturation,
                    heartRateStatus,
                    oxygenStatus,
                    timestamp,
                    confidenceLevel
                )
                
                _heartRateResult.value = heartRateResult
                _spO2Result.value = spO2Result
                _vitalSignsResult.value = vitalSignsResult
                _measurementState.value = MeasurementState.Complete
            } catch (e: Exception) {
                Log.e(TAG, "Error calculating vital signs", e)
                _measurementState.value = MeasurementState.Error
            }
        }
    }
    
    fun processFrame(bitmap: Bitmap) {
        if (_measurementState.value == MeasurementState.Measuring || 
            _measurementState.value == MeasurementState.Preparing) {
            try {
                val colorValues = SignalProcessingUtils.extractColorValues(bitmap)
                val redAvg = colorValues.red
                val greenAvg = colorValues.green
                val blueAvg = colorValues.blue
                val brightness = colorValues.brightness
                
                val signalQualityScore = if (redValues.size >= 10) {
                    PpgSignalProcessor.calculateSignalQuality(redValues.takeLast(10))
                } else {
                    0.5
                }
                
                val fingerDetected = brightness > 50 && redAvg > greenAvg * 1.5 && redAvg > blueAvg * 1.8 && redAvg > 180
                
                _signalQuality.value = when {
                    !fingerDetected -> SignalQuality.UNKNOWN
                    signalQualityScore >= 0.9 -> SignalQuality.GOOD
                    signalQualityScore >= 0.7 -> SignalQuality.GOOD
                    signalQualityScore >= 0.5 -> SignalQuality.POOR
                    else -> SignalQuality.TOO_DARK
                }
                
                if (_measurementState.value == MeasurementState.Measuring && fingerDetected) {
                    val filteredRed = SignalProcessingUtils.applyInitialFilter(redAvg)
                    val filteredGreen = SignalProcessingUtils.applyInitialFilter(greenAvg)
                    val filteredBlue = SignalProcessingUtils.applyInitialFilter(blueAvg)
                    
                    redValues.add(filteredRed)
                    greenValues.add(filteredGreen)
                    blueValues.add(filteredBlue)
                    
                    if (redValues.size >= 2) {
                        val variation = abs(redValues.last() - redValues[redValues.size - 2])
                        frameToFrameVariation.add(variation)
                        
                        if (redValues.size % 15 == 0) {
                            _currentBpmEstimate.value = getCurrentBpmEstimate()
                        }
                        
                        if (redValues.size > 10) {
                            val recentData = redValues.takeLast(100)
                            val filteredData = SignalProcessingUtils.enhancedBandpassFilter(recentData)
                            _ppgSignalData.value = filteredData
                            
                            if (variation > minSignalAmplitude && redValues.size >= 3) {
                                val lastThree = redValues.takeLast(3)
                                if (lastThree[1] > lastThree[0] && lastThree[1] > lastThree[2]) {
                                    _lastPulseTimestamp.value = System.currentTimeMillis()
                                }
                            }
                        }
                    }
                }
                
                _isFingerDetected.value = fingerDetected
                
            } catch (e: Exception) {
                Log.e(TAG, "Error processing frame", e)
            }
        }
    }
    
    fun startMeasurement() {
        _measurementState.value = MeasurementState.Preparing
        _progress.value = 0
        _signalQuality.value = SignalQuality.UNKNOWN
        
        redValues.clear()
        greenValues.clear()
        blueValues.clear()
        frameToFrameVariation.clear()
        
        viewModelScope.launch {
            if (_isFingerDetected.value) {
                _measurementState.value = MeasurementState.Measuring
                _progress.value = 20
            } else {
                for (i in 1..20) {
                    delay(250)
                    _progress.value = i
                    if (_isFingerDetected.value) {
                        break
                    }
                }
                _measurementState.value = MeasurementState.Measuring
            }
            
            for (i in 21..100) {
                delay(437)
                _progress.value = i
                
                if (!_isFingerDetected.value && redValues.size > 10) {
                    validateFingerPresence()
                }
            }
            
            _measurementState.value = MeasurementState.Processing
            delay(1000)
            
            if (redValues.size > 30 && _isFingerDetected.value) {
                val signalQualityScore = PpgSignalProcessor.calculateSignalQuality(redValues)
                if (signalQualityScore >= signalQualityThreshold) {
                    calculateVitalSigns()
                } else {
                    _measurementState.value = MeasurementState.Error
                }
            } else {
                _measurementState.value = MeasurementState.Error
            }
        }
    }
    
    fun cancelMeasurement() {
        if (_measurementState.value == MeasurementState.Measuring || 
            _measurementState.value == MeasurementState.Preparing) {
            _measurementState.value = MeasurementState.Idle
            _progress.value = 0
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        if (_measurementState.value == MeasurementState.Measuring || 
            _measurementState.value == MeasurementState.Preparing) {
            _measurementState.value = MeasurementState.Idle
        }
    }
    
    fun resetMeasurement() {
        _measurementState.value = MeasurementState.Idle
        _progress.value = 0
        _signalQuality.value = SignalQuality.UNKNOWN
        _isFingerDetected.value = false
        
        redValues.clear()
        greenValues.clear()
        blueValues.clear()
        frameToFrameVariation.clear()
        
        _heartRateResult.value = null
        _spO2Result.value = null
        _vitalSignsResult.value = null
    }
    
    fun clearResult() {
        _heartRateResult.value = null
        _spO2Result.value = null
        _vitalSignsResult.value = null
    }
}

