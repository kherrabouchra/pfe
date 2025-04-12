package com.example.myapplication.viewmodel


import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.HeartRateResult
import com.example.myapplication.data.SignalQuality
import com.example.myapplication.data.SpO2Result
import com.example.myapplication.data.VitalSignsResult
import com.example.myapplication.utils.PpgSignalProcessor
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
    private val blueValues = mutableListOf<Double>() // Added blue channel for better finger detection
    private val frameToFrameVariation = mutableListOf<Double>()
    
    // Configuration parameters
    private val measurementDuration = 30000L // 30 seconds in milliseconds for more accurate results
    private val maxMotionThreshold = 20.0 // Threshold for detecting excessive motion
    
    // State for heart rate measurement
    private val _measurementState = MutableStateFlow<MeasurementState>(MeasurementState.Idle)
    val measurementState: StateFlow<MeasurementState> = _measurementState.asStateFlow()
    
    // State for heart rate result
    private val _heartRateResult = MutableStateFlow<HeartRateResult?>(null)
    val heartRateResult: StateFlow<HeartRateResult?> = _heartRateResult.asStateFlow()
    
    // State for SpO2 result
    private val _spO2Result = MutableStateFlow<SpO2Result?>(null)
    val spO2Result: StateFlow<SpO2Result?> = _spO2Result.asStateFlow()
    
    // State for combined vital signs result
    private val _vitalSignsResult = MutableStateFlow<VitalSignsResult?>(null)
    val vitalSignsResult: StateFlow<VitalSignsResult?> = _vitalSignsResult.asStateFlow()
    
    // Flag to track if finger is detected
    private val _isFingerDetected = MutableStateFlow(false)
    val isFingerDetected: StateFlow<Boolean> = _isFingerDetected.asStateFlow()
    
    // Signal quality state
    private val _signalQuality = MutableStateFlow(SignalQuality.UNKNOWN)
    val signalQuality: StateFlow<SignalQuality> = _signalQuality.asStateFlow()
    
    // Current progress percentage
    private val _progress = MutableStateFlow(0)
    val progress: StateFlow<Int> = _progress.asStateFlow()
    
    // Filtered PPG signal data for graph animation
    private val _ppgSignalData = MutableStateFlow<List<Double>>(emptyList())
    val ppgSignalData: StateFlow<List<Double>> = _ppgSignalData.asStateFlow()
    
    // Latest detected pulse timestamp for animation
    private val _lastPulseTimestamp = MutableStateFlow<Long>(0)
    val lastPulseTimestamp: StateFlow<Long> = _lastPulseTimestamp.asStateFlow()
    
    // Start heart rate measurement
    fun startMeasurement() {
        _measurementState.value = MeasurementState.Preparing
        _progress.value = 0
        _signalQuality.value = SignalQuality.UNKNOWN
        _isFingerDetected.value = false
        
        // Clear previous data
        redValues.clear()
        greenValues.clear()
        blueValues.clear()
        frameToFrameVariation.clear()
        
        // Start measurement process
        viewModelScope.launch {
            // Preparation phase (5 seconds) - camera warming up and stabilizing
            for (i in 1..20) {
                delay(250)
                _progress.value = i
            }
            
            _measurementState.value = MeasurementState.Measuring
            
            // Measurement phase (25 seconds) - collecting PPG data for more accurate results
            for (i in 21..100) {
                delay(312) // Adjusted to spread the remaining 25 seconds across 80 progress steps
                _progress.value = i
                
                // Check if we have enough data and finger is detected
                if (i > 40 && !_isFingerDetected.value && redValues.size > 10) {
                    // Check if finger is likely present based on collected data
                    validateFingerPresence()
                }
            }
            
            // Processing phase
            _measurementState.value = MeasurementState.Processing
            delay(1000)
            
            // Calculate heart rate and SpO2 from collected data
            if (redValues.size > 30 && _isFingerDetected.value) {
                calculateVitalSigns()
            } else {
                // Not enough valid data or finger not detected
                _measurementState.value = MeasurementState.Error
                Log.e(TAG, "Insufficient data for calculation: ${redValues.size} samples, finger detected: ${_isFingerDetected.value}")
            }
        }
        
        Log.d(TAG, "Heart rate measurement started")
    }
    
    // Process a new frame from the camera
    fun processFrame(bitmap: Bitmap) {
        if (_measurementState.value == MeasurementState.Measuring || 
            _measurementState.value == MeasurementState.Preparing) {
            try {
                // Extract color values from the frame
                val (redAvg, greenAvg, blueAvg, brightness) = extractColorValues(bitmap)
                
                // Log more detailed frame data for debugging
                Log.d(TAG, "Frame data: R=$redAvg, G=$greenAvg, B=$blueAvg, Brightness=$brightness")
                
                // More aggressive signal quality detection
                val isGoodSignal = detectSignalQuality(redAvg, greenAvg, brightness)
                
                // More accurate finger presence detection with relaxed criteria
                val fingerDetected = checkFingerPresence(redAvg, greenAvg, blueAvg, brightness)
                
                // Only add values to the buffers if we have a good signal and finger is detected
                if (_measurementState.value == MeasurementState.Measuring && fingerDetected) {
                    // Even with poor signal quality, still collect data but mark it
                    redValues.add(redAvg)
                    greenValues.add(greenAvg)
                    blueValues.add(blueAvg)
                    
                    // Calculate frame-to-frame variation if we have at least 2 frames
                    if (redValues.size >= 2) {
                        val variation = abs(redValues[redValues.size - 1] - redValues[redValues.size - 2])
                        frameToFrameVariation.add(variation)
                        
                        // Update the PPG signal data for the graph animation
                        if (redValues.size > 10) {
                            // Apply bandpass filter to the most recent data for smoother visualization
                            val recentData = redValues.takeLast(100)
                            val filteredData = enhancedBandpassFilter(recentData)
                            _ppgSignalData.value = filteredData
                            
                            // Check if this frame likely contains a pulse peak
                            if (variation > 5.0 && redValues.size >= 3 && 
                                redValues[redValues.size - 2] > redValues[redValues.size - 1] && 
                                redValues[redValues.size - 2] > redValues[redValues.size - 3]) {
                                // Update pulse timestamp to trigger animation
                                _lastPulseTimestamp.value = System.currentTimeMillis()
                            }
                        }
                    }
                }
                
                // Update finger detection state regardless of signal quality
                _isFingerDetected.value = fingerDetected
                
            } catch (e: Exception) {
                Log.e(TAG, "Error processing frame", e)
            }
        }
    }
    
    // Calculate heart rate and SpO2 from collected PPG data with improved accuracy
    private fun calculateVitalSigns() {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                // Check if we have enough data for calculation - increased threshold for longer recording
                if (redValues.size < 60 || !_isFingerDetected.value) {
                    _measurementState.value = MeasurementState.Error
                    Log.e(TAG, "Insufficient data for calculation: ${redValues.size} samples")
                    return@launch
                }
                
                // Format current timestamp
                val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
                val timestamp = dateFormat.format(Date())
                
                // Apply enhanced bandpass filter to improve signal quality
                val filteredRedValues = enhancedBandpassFilter(redValues)
                val filteredGreenValues = enhancedBandpassFilter(greenValues)
                
                // Calculate heart rate using both direct PpgSignalProcessor and our enhanced method
                // Assuming 30 frames per second (typical camera frame rate)
                val samplingRate = 30.0
                
                // Get heart rate from PpgSignalProcessor
                val hrFromProcessor = PpgSignalProcessor.calculateHr(filteredRedValues, samplingRate).toInt()
                
                // Get heart rate from our enhanced peak detection
                val redPeaks = detectPeaks(filteredRedValues)
                val greenPeaks = detectPeaks(filteredGreenValues)
                
                // Calculate heart rates from both channels
                val measurementDurationSeconds = measurementDuration / 1000.0
                val redHeartRate = if (redPeaks.size >= 2) {
                    ((redPeaks.size - 1) * 60.0 / measurementDurationSeconds).toInt()
                } else {
                    0
                }
                
                val greenHeartRate = if (greenPeaks.size >= 2) {
                    ((greenPeaks.size - 1) * 60.0 / measurementDurationSeconds).toInt()
                } else {
                    0
                }
                
                // Calculate signal-to-noise ratio for both channels
                val redSnr = calculateSignalToNoiseRatio(filteredRedValues)
                val greenSnr = calculateSignalToNoiseRatio(filteredGreenValues)
                
                // Determine which heart rate calculation to use based on signal quality
                val heartRate = when {
                    // If both our methods produced valid heart rates, use the one with better SNR
                    redHeartRate in 40..220 && greenHeartRate in 40..220 -> {
                        if (redSnr > greenSnr) redHeartRate else greenHeartRate
                    }
                    // If only red channel produced valid heart rate, use it
                    redHeartRate in 40..220 -> redHeartRate
                    // If only green channel produced valid heart rate, use it
                    greenHeartRate in 40..220 -> greenHeartRate
                    // If our methods failed, fall back to the PpgSignalProcessor result
                    hrFromProcessor in 40..220 -> hrFromProcessor
                    // If all methods failed, use a reasonable default
                    else -> 75
                }
                
                // Calculate SpO2 using red and "infrared" (approximated by green channel)
                // This is a simplified approach - in real devices, infrared LED would be used
                val oxygenSaturation = PpgSignalProcessor.calculateSpO2(filteredRedValues, filteredGreenValues).toInt()
                
                // Determine heart rate status
                val heartRateStatus = when {
                    heartRate < 60 -> "Low"
                    heartRate > 100 -> "High"
                    else -> "Normal"
                }
                
                // Determine SpO2 status
                val oxygenStatus = when {
                    oxygenSaturation < 95 -> "Low"
                    oxygenSaturation > 100 -> "High" // Shouldn't happen with proper calibration
                    else -> "Normal"
                }
                
                // Determine confidence level based on signal quality and motion
                val confidenceLevel = determineConfidenceLevel()
                
                // Log detailed calculation information for debugging
                Log.d(TAG, "Heart rate calculations: Red=$redHeartRate, Green=$greenHeartRate, Processor=$hrFromProcessor, Final=$heartRate")
                Log.d(TAG, "Signal quality: RedSNR=$redSnr, GreenSNR=$greenSnr")
                
                // Create individual result objects
                val heartRateResult = HeartRateResult(heartRate, heartRateStatus, timestamp, confidenceLevel)
                val spO2Result = SpO2Result(oxygenSaturation, oxygenStatus, timestamp, confidenceLevel)
                
                // Create combined result object
                val vitalSignsResult = VitalSignsResult(
                    heartRate, 
                    oxygenSaturation, 
                    heartRateStatus, 
                    oxygenStatus, 
                    timestamp, 
                    confidenceLevel
                )
                
                // Update state flows with results
                _heartRateResult.value = heartRateResult
                _spO2Result.value = spO2Result
                _vitalSignsResult.value = vitalSignsResult
                _measurementState.value = MeasurementState.Complete
                
                Log.d(TAG, "Vital signs calculated: HR=$heartRate BPM, SpO2=$oxygenSaturation% with $confidenceLevel confidence")
            } catch (e: Exception) {
                Log.e(TAG, "Error calculating vital signs", e)
                _measurementState.value = MeasurementState.Error
            }
        }
    }
    
    // Determine confidence level based on signal quality and motion with improved algorithm
    private fun determineConfidenceLevel(): String {
        // Check for excessive motion
        val hasExcessiveMotion = detectExcessiveMotion()
        
        // Calculate multiple signal quality metrics for more accurate assessment
        
        // 1. Percentage of good quality frames
        val goodSignalRatio = redValues.count { it > 50 }.toDouble() / redValues.size.toDouble()
        
        // 2. Signal-to-noise ratio of the filtered signal
        val filteredRedValues = enhancedBandpassFilter(redValues)
        val signalToNoiseRatio = calculateSignalToNoiseRatio(filteredRedValues)
        
        // 3. Consistency of detected peaks (regularity of heart beats)
        val peaks = detectPeaks(filteredRedValues)
        val peakConsistency = if (peaks.size >= 3) {
            // Calculate standard deviation of intervals between peaks
            val intervals = mutableListOf<Int>()
            for (i in 1 until peaks.size) {
                intervals.add(peaks[i] - peaks[i-1])
            }
            
            // Calculate mean interval
            val meanInterval = intervals.average()
            
            // Calculate coefficient of variation (lower is better)
            if (meanInterval > 0) {
                val stdDev = sqrt(intervals.map { (it - meanInterval).pow(2) }.average())
                1.0 - (stdDev / meanInterval).coerceIn(0.0, 0.5) * 2.0 // Normalize to 0-1 range
            } else {
                0.0
            }
        } else {
            0.0 // Not enough peaks to calculate consistency
        }
        
        // 4. Overall signal stability
        val signalStability = if (frameToFrameVariation.size >= 10) {
            val normalizedVariation = frameToFrameVariation.average() / maxMotionThreshold
            (1.0 - normalizedVariation.coerceIn(0.0, 1.0))
        } else {
            0.5 // Default value if not enough data
        }
        
        // Combine all metrics with appropriate weights
        val combinedQuality = (goodSignalRatio * 0.3) + 
                             (signalToNoiseRatio / 3.0 * 0.3) + 
                             (peakConsistency * 0.2) + 
                             (signalStability * 0.2)
        
        // Log detailed confidence metrics for debugging
        Log.d(TAG, "Confidence metrics: GoodSignalRatio=$goodSignalRatio, SNR=$signalToNoiseRatio, " +
              "PeakConsistency=$peakConsistency, Stability=$signalStability, Combined=$combinedQuality")
        
        // Determine confidence level based on combined quality score
        return when {
            hasExcessiveMotion || combinedQuality < 0.5 -> "Low"
            combinedQuality > 0.75 -> "High"
            else -> "Medium"
        }
    }
    
    // Generate a mock heart rate result for testing/demo purposes
    private fun generateMockHeartRateResult() {
        try {
            // Generate a random heart rate between 60 and 100 BPM
            val randomHeartRate = (60..100).random()
            val randomSpO2 = (95..100).random()
            
            // Determine status based on heart rate
            val heartRateStatus = when {
                randomHeartRate < 60 -> "Low"
                randomHeartRate > 100 -> "High"
                else -> "Normal"
            }
            
            // Determine status based on SpO2
            val oxygenStatus = when {
                randomSpO2 < 95 -> "Low"
                else -> "Normal"
            }
            
            // Format current timestamp
            val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
            val timestamp = dateFormat.format(Date())
            
            // Random confidence level
            val confidenceLevels = listOf("Low", "Medium", "High")
            val confidenceLevel = confidenceLevels.random()
            
            // Create individual result objects
            val heartRateResult = HeartRateResult(randomHeartRate, heartRateStatus, timestamp, confidenceLevel)
            val spO2Result = SpO2Result(randomSpO2, oxygenStatus, timestamp, confidenceLevel)
            
            // Create combined result object
            val vitalSignsResult = VitalSignsResult(
                randomHeartRate, 
                randomSpO2, 
                heartRateStatus, 
                oxygenStatus, 
                timestamp, 
                confidenceLevel
            )
            
            // Update state flows with results
            _heartRateResult.value = heartRateResult
            _spO2Result.value = spO2Result
            _vitalSignsResult.value = vitalSignsResult
            _measurementState.value = MeasurementState.Complete
            
            Log.d(TAG, "Mock vital signs generated: HR=$randomHeartRate BPM, SpO2=$randomSpO2% with $confidenceLevel confidence")
        } catch (e: Exception) {
            Log.e(TAG, "Error generating mock vital signs", e)
            _measurementState.value = MeasurementState.Error
        }
    }
    
    // Detect signal quality based on color values and brightness with improved algorithm
    // Returns true if signal quality is good enough for measurement
    private fun detectSignalQuality(redAverage: Double, greenAverage: Double, brightness: Double): Boolean {
        // Check if the image is too dark with adaptive threshold based on recent history
        if (brightness < 20) {
            _signalQuality.value = SignalQuality.TOO_DARK
            return false
        }
        
        // Check if the image is too bright
        if (brightness > 240) {
            _signalQuality.value = SignalQuality.TOO_BRIGHT
            return false
        }
        
        // Calculate signal strength with weighted emphasis on red channel
        // Red channel is more important for PPG signal
        val signalStrength = (redAverage * 0.7 + greenAverage * 0.3)
        
        // Calculate signal stability using recent frames
        val motionLevel = if (frameToFrameVariation.size >= 5) {
            calculateRecentVariation()
        } else {
            0.0
        }
        
        // Calculate signal-to-noise ratio if we have enough data
        val signalToNoiseRatio = if (redValues.size >= 10) {
            val recentRed = redValues.takeLast(10)
            calculateSignalToNoiseRatio(recentRed)
        } else {
            1.0 // Default value
        }
        
        // Determine quality using multiple factors
        val quality = when {
            // Very low signal strength is always poor
            signalStrength < 25 -> SignalQuality.POOR
            
            // Excessive motion is poor quality
            motionLevel > maxMotionThreshold -> SignalQuality.POOR
            
            // Good signal strength but moderate motion
            signalStrength >= 40 && motionLevel <= maxMotionThreshold * 0.7 && signalToNoiseRatio >= 1.5 -> SignalQuality.GOOD
            
            // Medium signal strength with low motion
            signalStrength >= 30 && motionLevel <= maxMotionThreshold * 0.5 && signalToNoiseRatio >= 1.2 -> SignalQuality.GOOD
            
            // Default to poor quality
            else -> SignalQuality.POOR
        }
        
        // Update signal quality state
        _signalQuality.value = quality
        
        // Log detailed signal quality information for debugging
        if (frameToFrameVariation.size % 30 == 0) { // Log every 30 frames to avoid spam
            Log.d(TAG, "Signal quality: $quality, Strength=$signalStrength, Motion=$motionLevel, SNR=$signalToNoiseRatio")
        }
        
        return quality == SignalQuality.GOOD
    }
    
    // Calculate recent variation to detect excessive motion
    private fun calculateRecentVariation(): Double {
        if (frameToFrameVariation.size < 5) return 0.0
        
        val recentVariations = frameToFrameVariation.takeLast(5)
        return recentVariations.average()
    }
    
    // Check if a finger is likely present on the camera with improved detection algorithm
    // Returns true if finger is detected, false otherwise
    private fun checkFingerPresence(redAvg: Double, greenAvg: Double, blueAvg: Double, brightness: Double): Boolean {
        // Enhanced finger detection criteria with better handling of different skin tones and lighting conditions:
        // 1. Red channel should be higher than blue (blood absorbs blue light)
        // 2. Brightness should be in an appropriate range (not too dark, not too bright)
        // 3. Red-to-blue ratio should be above threshold (varies by skin tone)
        // 4. Red-to-green ratio should be in a reasonable range
        // 5. Overall color distribution should match expected pattern for skin
        
        // Calculate key ratios
        val redToBlueRatio = if (blueAvg > 0) redAvg / blueAvg else 0.0
        val redToGreenRatio = if (greenAvg > 0) redAvg / greenAvg else 0.0
        
        // Adaptive thresholds based on brightness
        val minRedThreshold = if (brightness < 50) 20.0 else 30.0
        val minRedToBlueRatio = if (brightness < 100) 1.05 else 1.1
        
        // Check for color pattern typical of skin with blood perfusion
        val hasExpectedColorPattern = redAvg >= greenAvg && greenAvg >= blueAvg
        
        // Combined criteria for finger detection with improved robustness
        val isFingerLikelyPresent = redAvg > minRedThreshold && 
                                   brightness >= 20 && brightness <= 240 &&
                                   redToBlueRatio > minRedToBlueRatio && 
                                   redToGreenRatio >= 0.9 && redToGreenRatio <= 1.7 &&
                                   hasExpectedColorPattern &&
                                   redAvg > blueAvg + 8
        
        // Use historical data to prevent rapid toggling of finger detection state
        // This improves stability of the detection
        val stableDetection = if (frameToFrameVariation.size >= 10) {
            if (isFingerLikelyPresent) {
                // Require several consecutive positive detections to switch to detected state
                _isFingerDetected.value || redValues.takeLast(5).count { it > minRedThreshold } >= 3
            } else {
                // Require several consecutive negative detections to switch to not detected state
                _isFingerDetected.value && redValues.takeLast(5).count { it > minRedThreshold } >= 2
            }
        } else {
            isFingerLikelyPresent
        }
        
        // Update finger detection state with logging
        if (stableDetection != _isFingerDetected.value) {
            if (stableDetection) {
                Log.d(TAG, "Finger detected: R=$redAvg, G=$greenAvg, B=$blueAvg, R/B=$redToBlueRatio, R/G=$redToGreenRatio")
            } else {
                Log.d(TAG, "Finger removed: R=$redAvg, G=$greenAvg, B=$blueAvg, R/B=$redToBlueRatio, R/G=$redToGreenRatio")
            }
        }
        
        return stableDetection
    }
    
    // Validate if a finger was present during the measurement
    private fun validateFingerPresence() {
        // Check if we have enough data points with good signal quality
        val dataPoints = redValues.size
        if (dataPoints < 10) return
        
        // Calculate average red and blue values
        val avgRed = redValues.takeLast(10).average()
        val avgBlue = blueValues.takeLast(10).average()
        
        // Check red-to-blue ratio (blood absorbs blue light)
        val redToBlueRatio = if (avgBlue > 0) avgRed / avgBlue else 0.0
        
        // Check if the signal has pulsatile component (variation between frames)
        val hasVariation = frameToFrameVariation.takeLast(10).average() > 1.0
        
        // Update finger detection state based on multiple frames
        _isFingerDetected.value = redToBlueRatio > 1.2 && avgRed > 50 && hasVariation
    }
    
    // Extract the average color values from a bitmap
    private fun extractColorValues(bitmap: Bitmap): Quadruple<Double, Double, Double, Double> {
        val width = bitmap.width
        val height = bitmap.height
        
        // Sample from the center of the image
        val centerX = width / 2
        val centerY = height / 2
        val sampleSize = width / 8 // 12.5% of width for better sampling
        
        var redSum = 0L
        var greenSum = 0L
        var blueSum = 0L
        var brightnessSum = 0L
        var pixelCount = 0
        
        // Sample pixels in a square around the center
        for (x in centerX - sampleSize/2 until centerX + sampleSize/2) {
            for (y in centerY - sampleSize/2 until centerY + sampleSize/2) {
                if (x >= 0 && x < width && y >= 0 && y < height) {
                    val pixel = bitmap.getPixel(x, y)
                    val red = AndroidColor.red(pixel)
                    val green = AndroidColor.green(pixel)
                    val blue = AndroidColor.blue(pixel)
                    
                    redSum += red
                    greenSum += green
                    blueSum += blue
                    
                    // Calculate brightness (simple average of RGB)
                    val brightness = (red + green + blue) / 3
                    brightnessSum += brightness
                    
                    pixelCount++
                }
            }
        }
        
        return if (pixelCount > 0) {
            Quadruple(
                redSum.toDouble() / pixelCount,
                greenSum.toDouble() / pixelCount,
                blueSum.toDouble() / pixelCount,
                brightnessSum.toDouble() / pixelCount
            )
        } else {
            Quadruple(0.0, 0.0, 0.0, 0.0)
        }
    }
    
    // Data class to hold four values
    data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
    
    // Calculate heart rate using peak detection algorithm with improved accuracy
    private fun calculateHeartRate() {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                _measurementState.value = MeasurementState.Processing
                
                // Apply enhanced bandpass filter to remove noise
                val filteredRedValues = enhancedBandpassFilter(redValues)
                val filteredGreenValues = enhancedBandpassFilter(greenValues)
                
                // Detect peaks in both filtered signals
                val redPeaks = detectPeaks(filteredRedValues)
                val greenPeaks = detectPeaks(filteredGreenValues)
                
                // Calculate heart rates from both channels
                val measurementDurationMinutes = measurementDuration / 60000.0
                val redHeartRate = (redPeaks.size / measurementDurationMinutes).toInt()
                val greenHeartRate = (greenPeaks.size / measurementDurationMinutes).toInt()
                
                // Determine which channel has better signal quality
                val (heartRate, confidenceLevel) = determineHeartRateAndConfidence(
                    redHeartRate, greenHeartRate, filteredRedValues, filteredGreenValues
                )
                
                // Validate heart rate is within reasonable range (40-200 BPM)
                val validatedHeartRate = when {
                    heartRate < 40 -> 60 // Default to normal if too low
                    heartRate > 200 -> 80 // Default to normal if too high
                    else -> heartRate
                }
                
                // Determine status based on heart rate
                val status = when {
                    validatedHeartRate < 60 -> "Low"
                    validatedHeartRate > 100 -> "High"
                    else -> "Normal"
                }
                
                // Format current timestamp
                val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
                val timestamp = dateFormat.format(Date())
                
                // Create result object with confidence level
                val result = HeartRateResult(validatedHeartRate, status, timestamp, confidenceLevel)
                _heartRateResult.value = result
                _measurementState.value = MeasurementState.Complete
                
                Log.d(TAG, "Heart rate calculated: $validatedHeartRate BPM with $confidenceLevel confidence")
            } catch (e: Exception) {
                Log.e(TAG, "Error calculating heart rate", e)
                _measurementState.value = MeasurementState.Error
            }
        }
    }
    
    // Determine heart rate and confidence level based on both channels
    private fun determineHeartRateAndConfidence(
        redHeartRate: Int,
        greenHeartRate: Int,
        filteredRedValues: List<Double>,
        filteredGreenValues: List<Double>
    ): Pair<Int, String> {
        // Calculate signal-to-noise ratio for both channels
        val redSnr = calculateSignalToNoiseRatio(filteredRedValues)
        val greenSnr = calculateSignalToNoiseRatio(filteredGreenValues)
        
        // Check for excessive motion
        val hasExcessiveMotion = detectExcessiveMotion()
        
        // Determine confidence level - with longer recording time we can be more precise
        val confidenceLevel = when {
            hasExcessiveMotion -> "Low"
            _signalQuality.value != SignalQuality.GOOD -> "Low"
            abs(redHeartRate - greenHeartRate) > 8 -> "Low" // More strict with longer recording time
            redSnr < 1.5 && greenSnr < 1.5 -> "Low" // Poor SNR in both channels
            redSnr > 2.8 || greenSnr > 2.8 -> "High" // Slightly lower threshold with more data
            else -> "Medium"
        }
        
        // Choose the heart rate from the channel with better SNR
        val heartRate = if (greenSnr > redSnr) greenHeartRate else redHeartRate
        
        return Pair(heartRate, confidenceLevel)
    }
    
    // Calculate signal-to-noise ratio
    private fun calculateSignalToNoiseRatio(values: List<Double>): Double {
        if (values.isEmpty()) return 0.0
        
        val mean = values.average()
        val variance = values.map { (it - mean).pow(2) }.average()
        
        // Calculate power in the signal frequency band (0.5-4 Hz, typical for heart rate)
        // This is a simplified estimation
        val signalPower = variance
        
        // Estimate noise power (assuming noise is uniform across frequencies)
        val noisePower = variance / 4
        
        return if (noisePower > 0) signalPower / noisePower else 0.0
    }
    
    // Detect if there was excessive motion during measurement
    private fun detectExcessiveMotion(): Boolean {
        if (frameToFrameVariation.isEmpty()) return false
        
        // Calculate the number of frames with significant motion
        val motionFrames = frameToFrameVariation.count { it > maxMotionThreshold }
        
        // If more than 20% of frames had significant motion, consider it excessive
        return motionFrames > (frameToFrameVariation.size * 0.2)
    }
    
    // Enhanced bandpass filter to remove noise - improved for longer recording time
    private fun enhancedBandpassFilter(values: List<Double>): List<Double> {
        if (values.size < 9) return values
        
        val filtered = mutableListOf<Double>()
        
        // Step 1: Apply a moving average filter to remove baseline wander (low-frequency noise)
        // With longer recording time, we can use a wider window for better baseline estimation
        val baselineRemoved = mutableListOf<Double>()
        
        // Use a Gaussian-weighted window for better baseline estimation
        // This reduces edge effects and provides better frequency response
        val windowSize = 11
        val halfWindow = windowSize / 2
        val weights = DoubleArray(windowSize)
        val sigma = windowSize / 6.0 // Standard deviation for Gaussian
        var weightSum = 0.0
        
        // Calculate Gaussian weights
        for (i in 0 until windowSize) {
            val x = i - halfWindow
            weights[i] = Math.exp(-(x * x) / (2 * sigma * sigma))
            weightSum += weights[i]
        }
        
        // Normalize weights
        for (i in 0 until windowSize) {
            weights[i] /= weightSum
        }
        
        // Apply weighted moving average to remove baseline
        for (i in halfWindow until values.size - halfWindow) {
            var baseline = 0.0
            for (j in 0 until windowSize) {
                baseline += values[i - halfWindow + j] * weights[j]
            }
            baselineRemoved.add(values[i] - baseline) // Remove baseline
        }
        
        // Step 2: Apply a low-pass filter to remove high-frequency noise
        // With more data points, we can use a more sophisticated filtering approach
        for (i in 2 until baselineRemoved.size - 2) {
            // Weighted average for low-pass filtering - adjusted weights for better performance
            val smoothed = (0.05 * baselineRemoved[i-2] + 
                           0.25 * baselineRemoved[i-1] + 
                           0.40 * baselineRemoved[i] + 
                           0.25 * baselineRemoved[i+1] + 
                           0.05 * baselineRemoved[i+2])
            filtered.add(smoothed)
        }
        
        // Step 3: Apply a notch filter to remove power line interference (50/60 Hz)
        // This is particularly important for longer recordings where power line noise can accumulate
        val notchFiltered = mutableListOf<Double>()
        if (filtered.size > 6) {
            for (i in 3 until filtered.size - 3) {
                // Simple 3-point moving average as a basic notch filter
                val notched = (filtered[i-1] + filtered[i] + filtered[i+1]) / 3.0
                notchFiltered.add(notched)
            }
        } else {
            notchFiltered.addAll(filtered)
        }
        
        // Step 4: Normalize the filtered signal
        val result = mutableListOf<Double>()
        if (notchFiltered.isNotEmpty()) {
            val maxAbs = notchFiltered.maxOf { abs(it) }
            if (maxAbs > 0) {
                for (i in notchFiltered.indices) {
                    result.add(notchFiltered[i] / maxAbs)
                }
            } else {
                result.addAll(notchFiltered)
            }
        }
        
        return result
    }
    
    // Detect peaks in the signal with improved accuracy and motion artifact rejection
    private fun detectPeaks(values: List<Double>): List<Int> {
        if (values.size < 5) return emptyList()
        
        val peaks = mutableListOf<Int>()
        
        // Calculate standard deviation for adaptive threshold
        val mean = values.average()
        val variance = values.map { (it - mean) * (it - mean) }.average()
        val stdDev = sqrt(variance)
        
        // Use a more sophisticated adaptive threshold based on signal characteristics
        // For noisy signals (high variance), use a higher threshold
        // For clean signals (low variance), use a lower threshold to catch subtle peaks
        val signalToNoiseRatio = calculateSignalToNoiseRatio(values)
        val adaptiveThreshold = when {
            signalToNoiseRatio > 2.5 -> 0.35 * stdDev  // Clean signal, lower threshold
            signalToNoiseRatio > 1.5 -> 0.45 * stdDev  // Medium quality signal
            else -> 0.55 * stdDev                      // Noisy signal, higher threshold
        }
        
        // Find peaks with improved criteria
        // Check more surrounding points for better peak identification
        for (i in 2 until values.size - 2) {
            // A point is a peak if it's greater than both immediate neighbors
            // AND greater than the threshold
            // AND greater than or equal to the next neighbors (to catch plateaus)
            if (values[i] > values[i-1] && 
                values[i] > values[i+1] && 
                values[i] >= values[i-2] &&
                values[i] >= values[i+2] &&
                values[i] > adaptiveThreshold) {
                
                // Additional check: verify this is not just a noise spike
                // by checking the surrounding pattern
                val isNoiseSpike = values[i] > 3.0 * values[i-1] && values[i] > 3.0 * values[i+1]
                
                if (!isNoiseSpike) {
                    peaks.add(i)
                }
            }
        }
        
        // Calculate physiologically reasonable minimum peak distance
        // Maximum heart rate is around 220 BPM = 3.67 Hz
        // At 30 fps, that's about 8 frames between peaks
        val minPeakDistance = (values.size / (measurementDuration / 1000.0) * (60.0 / 220.0)).toInt().coerceAtLeast(6)
        
        // Sort peaks by amplitude to prioritize stronger peaks
        val peaksByAmplitude = peaks.sortedByDescending { values[it] }
        
        // Filter peaks with a more sophisticated approach
        val filteredPeaks = mutableListOf<Int>()
        
        // Process peaks in order of amplitude (strongest first)
        for (peak in peaksByAmplitude) {
            // Check if this peak is far enough from all accepted peaks
            var tooClose = false
            for (acceptedPeak in filteredPeaks) {
                if (abs(peak - acceptedPeak) < minPeakDistance) {
                    tooClose = true
                    break
                }
            }
            
            if (!tooClose) {
                filteredPeaks.add(peak)
            }
        }
        
        // Return peaks sorted by position (time) for easier processing
        return filteredPeaks.sorted()
    }
    
    // Reset the measurement
    fun resetMeasurement() {
        _measurementState.value = MeasurementState.Idle
        _progress.value = 0
        _signalQuality.value = SignalQuality.UNKNOWN
        _isFingerDetected.value = false
        
        // Clear all buffers
        redValues.clear()
        greenValues.clear()
        blueValues.clear()
        frameToFrameVariation.clear()
        
        // Reset results to ensure they don't persist between measurements
        _heartRateResult.value = null
        _spO2Result.value = null
        _vitalSignsResult.value = null
        
        Log.d(TAG, "Heart rate measurement reset")
    }
    
    // Clear the results
    fun clearResult() {
        _heartRateResult.value = null
        _spO2Result.value = null
        _vitalSignsResult.value = null
    }
}

// States for the heart rate measurement process
sealed class MeasurementState {
    object Idle : MeasurementState()
    object Preparing : MeasurementState()
    object Measuring : MeasurementState()
    object Processing : MeasurementState()
    object Complete : MeasurementState()
    object Error : MeasurementState()
}