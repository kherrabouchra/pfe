package com.example.myapplication.utils

import android.graphics.Bitmap
import android.graphics.Color
import kotlin.math.abs
import kotlin.math.sqrt
import kotlin.math.pow

/**
 * Data class for holding color values extracted from a frame
 */
data class ColorValues(val red: Double, val green: Double, val blue: Double, val brightness: Double)

object SignalProcessingUtils {
    
    /**
     * Extract average RGB values and brightness from a bitmap frame
     */
    fun extractColorValues(bitmap: Bitmap): ColorValues {
        var redSum = 0.0
        var greenSum = 0.0
        var blueSum = 0.0
        var brightnessSum = 0.0
        val numPixels = bitmap.width * bitmap.height
        
        // Process each pixel in the bitmap
        for (x in 0 until bitmap.width) {
            for (y in 0 until bitmap.height) {
                val pixel = bitmap.getPixel(x, y)
                redSum += Color.red(pixel).toDouble()
                greenSum += Color.green(pixel).toDouble()
                blueSum += Color.blue(pixel).toDouble()
                brightnessSum += (0.299 * Color.red(pixel) + 
                                0.587 * Color.green(pixel) + 
                                0.114 * Color.blue(pixel))
            }
        }
        
        // Calculate averages
        val redAvg = redSum / numPixels
        val greenAvg = greenSum / numPixels
        val blueAvg = blueSum / numPixels
        val brightnessAvg = brightnessSum / numPixels
        
        return ColorValues(redAvg, greenAvg, blueAvg, brightnessAvg)
    }
    
    /**
     * Apply initial filtering to raw signal values
     */
    fun applyInitialFilter(value: Double): Double {
        // Simple moving average filter
        return value
    }
    
    /**
     * Apply bandpass filter to PPG signal
     */
    fun enhancedBandpassFilter(values: List<Double>): List<Double> {
        if (values.isEmpty()) return emptyList()
        
        val filtered = mutableListOf<Double>()
        val windowSize = 5 // Adjust based on sampling rate and desired frequency response
        
        // Simple moving average filter
        for (i in values.indices) {
            val start = maxOf(0, i - windowSize + 1)
            val end = minOf(values.size, i + 1)
            val avg = values.subList(start, end).average()
            filtered.add(avg)
        }
        
        return filtered
    }
    
    /**
     * Detect peaks in PPG signal
     */
    fun detectPeaks(values: List<Double>): List<Int> {
        val peaks = mutableListOf<Int>()
        if (values.size < 3) return peaks
        
        // Calculate adaptive threshold based on signal amplitude
        val mean = values.average()
        val stdDev = sqrt(values.map { (it - mean).pow(2) }.average())
        val threshold = mean + 0.5 * stdDev
        
        // Enhanced peak detection with minimum distance and amplitude requirements
        val minPeakDistance = 20 // Minimum samples between peaks (for 30Hz sampling)
        var lastPeakIndex = -minPeakDistance
        
        for (i in 1 until values.size - 1) {
            if (values[i] > threshold && 
                values[i] > values[i-1] && 
                values[i] > values[i+1] && 
                i - lastPeakIndex >= minPeakDistance) {
                
                // Verify it's a true peak by checking neighboring points
                var isPeak = true
                val checkRange = 2
                
                for (j in 1..checkRange) {
                    if (i-j >= 0 && values[i] < values[i-j] ||
                        i+j < values.size && values[i] < values[i+j]) {
                        isPeak = false
                        break
                    }
                }
                
                if (isPeak) {
                    peaks.add(i)
                    lastPeakIndex = i
                }
            }
        }
        
        return peaks
    }
    
    /**
     * Calculate signal-to-noise ratio
     */
    fun calculateSignalToNoiseRatio(values: List<Double>): Double {
        if (values.size < 2) return 0.0
        
        val mean = values.average()
        val variance = values.map { (it - mean).pow(2) }.average()
        val noise = sqrt(variance)
        
        return if (noise > 0) mean / noise else 0.0
    }
    
    /**
     * Helper function for power calculation
     */
    private fun Double.pow(n: Int): Double = this.pow(n.toDouble())
}

// End of SignalProcessingUtils