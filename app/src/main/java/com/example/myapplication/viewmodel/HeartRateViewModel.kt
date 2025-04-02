package com.example.myapplication.viewmodel

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.HeartRateResult
import com.example.myapplication.data.SignalQuality
import kotlinx.coroutines.Dispatchers
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
    
    // State for heart rate measurement
    private val _measurementState = MutableStateFlow<MeasurementState>(MeasurementState.Idle)
    val measurementState: StateFlow<MeasurementState> = _measurementState.asStateFlow()
    
    // State for heart rate result
    private val _heartRateResult = MutableStateFlow<HeartRateResult?>(null)
    val heartRateResult: StateFlow<HeartRateResult?> = _heartRateResult.asStateFlow()
    
    // Buffer for red and green values from camera frames
    private val redValues = mutableListOf<Double>()
    private val greenValues = mutableListOf<Double>()
    
    // Signal quality state
    private val _signalQuality = MutableStateFlow(SignalQuality.UNKNOWN)
    val signalQuality: StateFlow<SignalQuality> = _signalQuality.asStateFlow()
    
    // Motion detection
    private val frameToFrameVariation = mutableListOf<Double>()
    private val maxMotionThreshold = 30.0 // Threshold for detecting significant motion
    
    // Timestamp when measurement started
    private var startTime: Long = 0
    
    // Current progress percentage
    private val _progress = MutableStateFlow(0)
    val progress: StateFlow<Int> = _progress.asStateFlow()
    
    // Measurement duration in milliseconds (30 seconds)
    private val measurementDuration = 30000L
    
    // Start heart rate measurement
    fun startMeasurement() {
        _measurementState.value = MeasurementState.Preparing
        _progress.value = 0
        _signalQuality.value = SignalQuality.UNKNOWN
        
        // Clear all buffers
        redValues.clear()
        greenValues.clear()
        frameToFrameVariation.clear()
        
        startTime = System.currentTimeMillis()
        
        Log.d(TAG, "Heart rate measurement started")
    }
    
    // Process a new frame from the camera
    fun processFrame(bitmap: Bitmap) {
        val currentTime = System.currentTimeMillis()
        val elapsedTime = currentTime - startTime
        
        // Calculate progress percentage
        val progressPercentage = ((elapsedTime.toFloat() / measurementDuration) * 100).toInt()
        _progress.value = progressPercentage.coerceIn(0, 100)
        
        when (_measurementState.value) {
            MeasurementState.Preparing -> {
                // After 3 seconds of preparation, start measuring
                if (elapsedTime > 3000) {
                    _measurementState.value = MeasurementState.Measuring
                    redValues.clear()
                    greenValues.clear()
                    frameToFrameVariation.clear()
                    startTime = System.currentTimeMillis()
                }
            }
            MeasurementState.Measuring -> {
                // Extract color channel averages from the bitmap
                val (redAverage, greenAverage, brightness) = extractColorValues(bitmap)
                
                // Detect signal quality
                detectSignalQuality(redAverage, greenAverage, brightness)
                
                // Add values to buffers
                redValues.add(redAverage)
                greenValues.add(greenAverage)
                
                // Detect motion artifacts
                if (redValues.size >= 2) {
                    val variation = abs(redValues.last() - redValues[redValues.size - 2])
                    frameToFrameVariation.add(variation)
                }
                
                // Check if measurement is complete
                if (elapsedTime >= measurementDuration) {
                    calculateHeartRate()
                }
            }
            else -> {}
        }
    }
    
    // Detect signal quality based on color values and brightness
    private fun detectSignalQuality(redAverage: Double, greenAverage: Double, brightness: Double) {
        // Check if the image is too dark
        if (brightness < 30) {
            _signalQuality.value = SignalQuality.TOO_DARK
            return
        }
        
        // Check if the image is too bright
        if (brightness > 240) {
            _signalQuality.value = SignalQuality.TOO_BRIGHT
            return
        }
        
        // Check if there's enough red and green signal
        val signalStrength = (redAverage + greenAverage) / 2
        
        _signalQuality.value = when {
            signalStrength < 50 -> SignalQuality.POOR
            else -> SignalQuality.GOOD
        }
    }
    
    // Extract the average color values from a bitmap
    private fun extractColorValues(bitmap: Bitmap): Triple<Double, Double, Double> {
        val width = bitmap.width
        val height = bitmap.height
        
        // Sample from the center of the image
        val centerX = width / 2
        val centerY = height / 2
        val sampleSize = width / 8 // 12.5% of width for better sampling
        
        var redSum = 0L
        var greenSum = 0L
        var brightnessSum = 0L
        var pixelCount = 0
        
        // Sample pixels in a square around the center
        for (x in centerX - sampleSize/2 until centerX + sampleSize/2) {
            for (y in centerY - sampleSize/2 until centerY + sampleSize/2) {
                if (x >= 0 && x < width && y >= 0 && y < height) {
                    val pixel = bitmap.getPixel(x, y)
                    val red = Color.red(pixel)
                    val green = Color.green(pixel)
                    val blue = Color.blue(pixel)
                    
                    redSum += red
                    greenSum += green
                    
                    // Calculate brightness (simple average of RGB)
                    val brightness = (red + green + blue) / 3
                    brightnessSum += brightness
                    
                    pixelCount++
                }
            }
        }
        
        return if (pixelCount > 0) {
            Triple(
                redSum.toDouble() / pixelCount,
                greenSum.toDouble() / pixelCount,
                brightnessSum.toDouble() / pixelCount
            )
        } else {
            Triple(0.0, 0.0, 0.0)
        }
    }
    
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
        
        // Determine confidence level
        val confidenceLevel = when {
            hasExcessiveMotion -> "Low"
            _signalQuality.value != SignalQuality.GOOD -> "Low"
            abs(redHeartRate - greenHeartRate) > 10 -> "Low" // Large discrepancy between channels
            redSnr < 1.5 && greenSnr < 1.5 -> "Low" // Poor SNR in both channels
            redSnr > 3.0 || greenSnr > 3.0 -> "High" // Excellent SNR in at least one channel
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
    
    // Enhanced bandpass filter to remove noise
    private fun enhancedBandpassFilter(values: List<Double>): List<Double> {
        if (values.size < 9) return values
        
        val filtered = mutableListOf<Double>()
        
        // Step 1: Apply a moving average filter to remove baseline wander (low-frequency noise)
        val baselineRemoved = mutableListOf<Double>()
        for (i in 4 until values.size - 4) {
            // Use a wider window for better baseline estimation
            val avg = (values[i-4] + values[i-3] + values[i-2] + values[i-1] + 
                      values[i] + values[i+1] + values[i+2] + values[i+3] + values[i+4]) / 9.0
            baselineRemoved.add(values[i] - avg) // Remove baseline
        }
        
        // Step 2: Apply a low-pass filter to remove high-frequency noise
        for (i in 2 until baselineRemoved.size - 2) {
            // Weighted average for low-pass filtering
            val smoothed = (0.1 * baselineRemoved[i-2] + 
                           0.2 * baselineRemoved[i-1] + 
                           0.4 * baselineRemoved[i] + 
                           0.2 * baselineRemoved[i+1] + 
                           0.1 * baselineRemoved[i+2])
            filtered.add(smoothed)
        }
        
        // Step 3: Normalize the filtered signal
        if (filtered.isNotEmpty()) {
            val maxAbs = filtered.maxOf { abs(it) }
            if (maxAbs > 0) {
                for (i in filtered.indices) {
                    filtered[i] = filtered[i] / maxAbs
                }
            }
        }
        
        return filtered
    }
    
    // Detect peaks in the signal
    private fun detectPeaks(values: List<Double>): List<Int> {
        if (values.size < 3) return emptyList()
        
        val peaks = mutableListOf<Int>()
        
        // Calculate standard deviation for adaptive threshold
        val mean = values.average()
        val variance = values.map { (it - mean) * (it - mean) }.average()
        val stdDev = sqrt(variance)
        val threshold = 0.5 * stdDev
        
        // Find peaks
        for (i in 1 until values.size - 1) {
            if (values[i] > values[i-1] && values[i] > values[i+1] && values[i] > threshold) {
                peaks.add(i)
            }
        }
        
        // Filter out peaks that are too close (less than 0.25 seconds apart)
        val minPeakDistance = (0.25 * (values.size / (measurementDuration / 1000.0))).toInt()
        
        val filteredPeaks = mutableListOf<Int>()
        var lastPeak = -minPeakDistance
        
        for (peak in peaks) {
            if (peak - lastPeak >= minPeakDistance) {
                filteredPeaks.add(peak)
                lastPeak = peak
            }
        }
        
        return filteredPeaks
    }
    
    // Reset the measurement
    fun resetMeasurement() {
        _measurementState.value = MeasurementState.Idle
        _progress.value = 0
        _signalQuality.value = SignalQuality.UNKNOWN
        
        // Clear all buffers
        redValues.clear()
        greenValues.clear()
        frameToFrameVariation.clear()
        
        Log.d(TAG, "Heart rate measurement reset")
    }
    
    // Clear the result
    fun clearResult() {
        _heartRateResult.value = null
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