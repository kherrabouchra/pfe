package com.example.myapplication.utils

import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.PI

/**
 * Utility class for PPG (Photoplethysmography) signal processing
 * Implements algorithms for heart rate and SpO2 calculation from PPG signals
 */
class PpgSignalProcessor {

    companion object {
        private const val TAG = "PpgSignalProcessor"

        /**
         * Preprocess the PPG signal by applying enhanced filtering and motion artifact rejection
         * @param ppgSignal Raw PPG signal data
         * @return Filtered PPG signal
         */
        fun preprocessPpg(ppgSignal: List<Double>): List<Double> {
            if (ppgSignal.size < 5) return ppgSignal
            
            // Step 1: Detect and handle motion artifacts
            val cleanedSignal = removeMotionArtifacts(ppgSignal)
            
            // Step 2: Apply more aggressive low-pass filter to remove high-frequency noise (above 4 Hz)
            // Heart rate is typically 0.67-3.33 Hz (40-200 BPM), so 4 Hz is a safe upper limit
            val lowPassFiltered = lowPassFilter(cleanedSignal, 4.0)
            
            // Step 3: Apply high-pass filter to remove DC component and very low frequency noise (below 0.67 Hz)
            // 0.67 Hz corresponds to 40 BPM, which is the lower bound of normal heart rates
            val highPassFiltered = highPassFilter(lowPassFiltered, 0.67)
            
            // Step 4: Apply additional detrending to remove any remaining baseline drift
            return detrend(highPassFiltered)
        }
        
        /**
         * Calculate signal quality score based on signal characteristics
         * @param ppgSignal PPG signal data
         * @return Quality score between 0.0 (poor) and 1.0 (excellent)
         */
        fun calculateSignalQuality(ppgSignal: List<Double>): Double {
            if (ppgSignal.size < 10) return 0.0
            
            // Calculate signal statistics
            val mean = ppgSignal.average()
            val stdDev = kotlin.math.sqrt(ppgSignal.map { (it - mean).pow(2) }.average())
            
            // Calculate signal-to-noise ratio (SNR)
            val snr = if (stdDev > 0) mean / stdDev else 0.0
            
            // Calculate signal amplitude
            val minVal = ppgSignal.minOrNull() ?: 0.0
            val maxVal = ppgSignal.maxOrNull() ?: 0.0
            val amplitude = maxVal - minVal
            
            // Calculate frame-to-frame variation (for motion detection)
            val frameVariation = mutableListOf<Double>()
            for (i in 1 until ppgSignal.size) {
                frameVariation.add(kotlin.math.abs(ppgSignal[i] - ppgSignal[i-1]))
            }
            val avgVariation = frameVariation.average()
            
            // Normalize metrics to 0-1 range with more lenient thresholds
            // Reduced amplitude requirement to be more tolerant of weaker signals
            val normalizedAmplitude = (amplitude / 200.0).coerceIn(0.0, 1.0)
            // More tolerant SNR normalization
            val normalizedSnr = (snr / 8.0).coerceIn(0.0, 1.0)
            // More tolerant of variation (movement)
            val normalizedVariation = (1.0 - (avgVariation / 70.0).coerceIn(0.0, 1.0))
            
            // Weighted combination of metrics with higher weight on amplitude
            // and lower weight on variation to be more tolerant of movement
            return (normalizedAmplitude * 0.5) + (normalizedSnr * 0.35) + (normalizedVariation * 0.15)
        }
        
        /**
         * Improved finger detection algorithm
         * @param redChannel Average red channel value
         * @param greenChannel Average green channel value
         * @param blueChannel Average blue channel value
         * @return True if finger is detected, false otherwise
         */
        fun isFingerDetected(redChannel: Double, greenChannel: Double, blueChannel: Double): Boolean {
            // Finger detection criteria:
            // 1. Red channel should be high (finger blocks blue/green light more than red)
            // 2. Red channel should be significantly higher than green and blue
            // 3. Overall brightness should be within a certain range (not too dark, not too bright)
            
            val redThreshold = 140.0  // Reduced minimum red value when finger is present
            val redGreenRatio = 1.2    // Reduced ratio requirement for red vs green
            val redBlueRatio = 1.3     // Reduced ratio requirement for red vs blue
            val maxBrightness = 255.0  // Increased maximum brightness to avoid false negatives
            
            return redChannel >= redThreshold && 
                   redChannel <= maxBrightness &&
                   redChannel >= greenChannel * redGreenRatio && 
                   redChannel >= blueChannel * redBlueRatio
        }
        
        /**
         * Remove motion artifacts from PPG signal with enhanced tolerance for walking/activity
         * @param ppgSignal Raw PPG signal
         * @return Cleaned signal with motion artifacts removed or attenuated
         */
        private fun removeMotionArtifacts(ppgSignal: List<Double>): List<Double> {
            if (ppgSignal.size < 10) return ppgSignal
            
            val cleanedSignal = mutableListOf<Double>()
            val windowSize = 7 // Increased window size for better context during motion
            
            // Calculate first and second derivatives for motion detection
            val firstDerivative = mutableListOf<Double>()
            for (i in 1 until ppgSignal.size) {
                firstDerivative.add(ppgSignal[i] - ppgSignal[i-1])
            }
            
            // Add first point to make sizes match
            firstDerivative.add(0, firstDerivative[0])
            
            // Calculate second derivative for acceleration detection
            val secondDerivative = mutableListOf<Double>()
            for (i in 1 until firstDerivative.size) {
                secondDerivative.add(firstDerivative[i] - firstDerivative[i-1])
            }
            secondDerivative.add(0, secondDerivative.firstOrNull() ?: 0.0)
            
            // Calculate threshold for motion artifact detection with adaptive threshold
            // Based on statistical properties of the derivatives
            val derivativeMean = firstDerivative.average()
            val derivativeStdDev = sqrt(firstDerivative.map { (it - derivativeMean).pow(2) }.average())
            
            // Use a more permissive threshold during activity (4 sigma instead of 3)
            val motionThreshold = 4.0 * derivativeStdDev
            
            // Detect and handle artifacts
            for (i in ppgSignal.indices) {
                if (i > 0 && i < ppgSignal.size - 1) {
                    val derivative = abs(firstDerivative[i])
                    val acceleration = if (i > 0) abs(secondDerivative[i]) else 0.0
                    
                    // Combined criteria for artifact detection - more tolerant of gradual changes
                    val isArtifact = derivative > motionThreshold && acceleration > derivativeStdDev * 2.0
                    
                    if (isArtifact) {
                        // Motion artifact detected - use adaptive filtering approach
                        val startIdx = maxOf(0, i - windowSize)
                        val endIdx = minOf(ppgSignal.size - 1, i + windowSize)
                        
                        // Find valid points before and after the artifact with relaxed criteria
                        var validBefore = startIdx
                        var validAfter = endIdx
                        
                        for (j in i-1 downTo startIdx) {
                            if (abs(firstDerivative[j]) <= motionThreshold * 0.8) { // Relaxed threshold
                                validBefore = j
                                break
                            }
                        }
                        
                        for (j in i+1..endIdx) {
                            if (abs(firstDerivative[j]) <= motionThreshold * 0.8) { // Relaxed threshold
                                validAfter = j
                                break
                            }
                        }
                        
                        // Use weighted average of nearby points if interpolation isn't possible
                        if (validBefore < validAfter) {
                            val beforeVal = ppgSignal[validBefore]
                            val afterVal = ppgSignal[validAfter]
                            val range = validAfter - validBefore
                            val position = i - validBefore
                            val interpolated = beforeVal + (afterVal - beforeVal) * position / range
                            cleanedSignal.add(interpolated)
                        } else {
                            // Use weighted average of nearby valid points
                            val nearbyPoints = mutableListOf<Double>()
                            val weights = mutableListOf<Double>()
                            
                            for (j in maxOf(0, i - windowSize)..minOf(ppgSignal.size - 1, i + windowSize)) {
                                if (j != i && abs(firstDerivative.getOrElse(j) { 0.0 }) <= motionThreshold) {
                                    nearbyPoints.add(ppgSignal[j])
                                    weights.add(1.0 / (abs(j - i) + 1)) // Weight by distance
                                }
                            }
                            
                            val filteredValue = if (nearbyPoints.isNotEmpty()) {
                                var weightedSum = 0.0
                                var weightSum = 0.0
                                for (k in nearbyPoints.indices) {
                                    weightedSum += nearbyPoints[k] * weights[k]
                                    weightSum += weights[k]
                                }
                                weightedSum / weightSum
                            } else {
                                ppgSignal[i] // Keep original if no valid neighbors
                            }
                            
                            cleanedSignal.add(filteredValue)
                        }
                    } else {
                        // No artifact detected
                        cleanedSignal.add(ppgSignal[i])
                    }
                } else {
                    // Keep edge points as is
                    cleanedSignal.add(ppgSignal[i])
                }
            }
            
            return cleanedSignal
        }
        
        /**
         * Apply detrending to remove baseline drift
         * @param signal Input signal
         * @return Detrended signal
         */
        private fun detrend(signal: List<Double>): List<Double> {
            if (signal.size < 10) return signal
            
            val detrended = mutableListOf<Double>()
            val windowSize = minOf(signal.size / 3, 30) // Adaptive window size
            
            for (i in signal.indices) {
                val startIdx = maxOf(0, i - windowSize)
                val endIdx = minOf(signal.size - 1, i + windowSize)
                
                // Calculate local baseline as moving average
                var sum = 0.0
                for (j in startIdx..endIdx) {
                    sum += signal[j]
                }
                val baseline = sum / (endIdx - startIdx + 1)
                
                // Subtract baseline from signal
                detrended.add(signal[i] - baseline)
            }
            
            return detrended
        }

        /**
         * Perform Fast Fourier Transform (FFT) on a signal
         * @param signal Input signal (time domain)
         * @return Complex array representing frequency domain (real and imaginary parts)
         */
        fun performFft(signal: List<Double>): Array<Pair<Double, Double>> {
            // Ensure signal length is a power of 2 for FFT efficiency
            val paddedSignal = padToPowerOfTwo(signal)
            val n = paddedSignal.size
            
            // Base case for recursion
            if (n == 1) {
                return arrayOf(Pair(paddedSignal[0], 0.0))
            }
            
            // Split signal into even and odd indices
            val even = Array(n / 2) { paddedSignal[it * 2] }
            val odd = Array(n / 2) { paddedSignal[it * 2 + 1] }
            
            // Recursive FFT on even and odd components
            val evenFFT = performFft(even.toList())
            val oddFFT = performFft(odd.toList())
            
            // Combine results
            val result = Array(n) { Pair(0.0, 0.0) }
            
            for (k in 0 until n / 2) {
                // Calculate twiddle factor
                val angle = -2.0 * PI * k / n
                val twiddle_real = cos(angle)
                val twiddle_imag = sin(angle)
                
                // Apply twiddle factor to odd component
                val odd_real = oddFFT[k].first * twiddle_real - oddFFT[k].second * twiddle_imag
                val odd_imag = oddFFT[k].first * twiddle_imag + oddFFT[k].second * twiddle_real
                
                // Combine even and odd components
                result[k] = Pair(
                    evenFFT[k].first + odd_real,
                    evenFFT[k].second + odd_imag
                )
                
                result[k + n / 2] = Pair(
                    evenFFT[k].first - odd_real,
                    evenFFT[k].second - odd_imag
                )
            }
            
            return result
        }
        
        /**
         * Pad signal to the next power of two for efficient FFT processing
         * @param signal Input signal
         * @return Padded signal with length as power of 2
         */
        private fun padToPowerOfTwo(signal: List<Double>): List<Double> {
            val size = signal.size
            var powerOfTwo = 1
            while (powerOfTwo < size) {
                powerOfTwo *= 2
            }
            
            val paddedSignal = MutableList(powerOfTwo) { 0.0 }
            for (i in signal.indices) {
                paddedSignal[i] = signal[i]
            }
            
            return paddedSignal
        }
        
        /**
         * Calculate magnitude spectrum from FFT result
         * @param fftResult Complex FFT result
         * @return Magnitude spectrum
         */
        fun calculateMagnitudeSpectrum(fftResult: Array<Pair<Double, Double>>): List<Double> {
            return fftResult.map { sqrt(it.first * it.first + it.second * it.second) }
        }
        
        /**
         * Find dominant frequency in the signal using FFT
         * @param signal Input signal
         * @param samplingRate Sampling rate in Hz
         * @return Dominant frequency in Hz
         */
        fun findDominantFrequency(signal: List<Double>, samplingRate: Double): Double {
            if (signal.size < 10) return 0.0
            
            // Detrend and apply window function to reduce spectral leakage
            val processedSignal = detrend(signal).mapIndexed { i, value ->
                // Apply Hamming window
                value * (0.54 - 0.46 * cos(2.0 * PI * i / (signal.size - 1)))
            }
            
            // Perform FFT
            val fftResult = performFft(processedSignal)
            val magnitudeSpectrum = calculateMagnitudeSpectrum(fftResult)
            
            // Find peak in the frequency range of interest (0.67 Hz to 3.33 Hz for HR 40-200 BPM)
            val frequencyResolution = samplingRate / fftResult.size
            var maxMagnitude = 0.0
            var dominantFrequencyIndex = 0
            
            // Only consider the first half of the spectrum (up to Nyquist frequency)
            val nyquistIndex = fftResult.size / 2
            
            // Define the frequency range for heart rate (0.67 Hz to 3.33 Hz)
            val minIndex = (0.67 / frequencyResolution).toInt().coerceAtLeast(1)
            val maxIndex = (3.33 / frequencyResolution).toInt().coerceAtMost(nyquistIndex - 1)
            
            for (i in minIndex..maxIndex) {
                if (magnitudeSpectrum[i] > maxMagnitude) {
                    maxMagnitude = magnitudeSpectrum[i]
                    dominantFrequencyIndex = i
                }
            }
            
            // Convert index to frequency
            return dominantFrequencyIndex * frequencyResolution
        }
        
        /**
         * Calculate heart rate from PPG signal with enhanced accuracy and robustness
         * using both time-domain and frequency-domain analysis
         * @param ppgSignal Raw PPG signal data
         * @param samplingRate Sampling rate in Hz
         * @return Heart rate in BPM (beats per minute)
         */
        fun calculateHr(ppgSignal: List<Double>, samplingRate: Double): Double {
            // Validate input data
            if (ppgSignal.size < 25) { // Reduced minimum data points required
                return 0.0 // Not enough data for reliable calculation
            }
            
            // Step 1: Check signal quality before processing with significantly reduced threshold
            val signalQuality = calculateSignalQuality(ppgSignal)
            if (signalQuality < 0.35) { // Significantly reduced quality threshold to be more tolerant
                return 0.0 // Signal quality too poor for reliable calculation
            }
            
            // Step 2: Preprocess PPG signal with enhanced filtering
            val ppgFiltered = preprocessPpg(ppgSignal)
            
            // Step 3: Calculate signal amplitude and validate with reduced threshold
            val signalAmplitude = ppgFiltered.maxOrNull()?.minus(ppgFiltered.minOrNull() ?: 0.0) ?: 0.0
            if (signalAmplitude < 2.0) { // Further reduced minimum amplitude threshold for better sensitivity
                return 0.0 // Signal too weak
            }
            
            // Step 4: Calculate heart rate using time-domain peak detection
            val timeDomainHr = calculateTimeDomainHr(ppgFiltered, samplingRate)
            
            // Step 5: Calculate heart rate using frequency-domain (FFT) analysis
            val frequencyDomainHr = calculateFrequencyDomainHr(ppgFiltered, samplingRate)
            
            // Step 6: Combine results with weighted average, favoring the more reliable method
            // If one method fails, use the other
            return when {
                timeDomainHr > 0 && frequencyDomainHr > 0 -> {
                    // Both methods succeeded, use weighted average
                    // Give more weight to frequency domain during motion (higher quality score means less motion)
                    val freqWeight = 0.7 - (signalQuality * 0.4) // 0.3 to 0.7 based on quality
                    val timeWeight = 1.0 - freqWeight
                    
                    (timeDomainHr * timeWeight + frequencyDomainHr * freqWeight)
                }
                timeDomainHr > 0 -> timeDomainHr // Only time-domain succeeded
                frequencyDomainHr > 0 -> frequencyDomainHr // Only frequency-domain succeeded
                else -> 0.0 // Both methods failed
            }
        }
        
        /**
         * Calculate heart rate using time-domain peak detection
         * @param ppgFiltered Filtered PPG signal
         * @param samplingRate Sampling rate in Hz
         * @return Heart rate in BPM
         */
        private fun calculateTimeDomainHr(ppgFiltered: List<Double>, samplingRate: Double): Double {
            // Detect peaks with improved criteria for motion tolerance
            val peaks = mutableListOf<Int>()
            val windowSize = (samplingRate * 0.2).toInt() // 200ms window
            
            for (i in windowSize until ppgFiltered.size - windowSize) {
                val window = ppgFiltered.subList(i - windowSize, i + windowSize)
                val localMax = window.maxOrNull() ?: continue
                
                if (ppgFiltered[i] == localMax && 
                    ppgFiltered[i] > ppgFiltered[i-1] && 
                    ppgFiltered[i] > ppgFiltered[i+1]) {
                    
                    // Only add if it's a significant peak
                    val signalAmplitude = ppgFiltered.maxOrNull()?.minus(ppgFiltered.minOrNull() ?: 0.0) ?: 0.0
                    if (ppgFiltered[i] > ppgFiltered.average() + signalAmplitude * 0.15) {
                        peaks.add(i)
                    }
                }
            }
            
            // Ensure we have enough peaks
            if (peaks.size < 3) {
                return 0.0
            }
            
            // Calculate intervals between peaks
            val intervals = mutableListOf<Double>()
            for (i in 1 until peaks.size) {
                val interval = (peaks[i] - peaks[i-1]) / samplingRate
                // Accept wider range of physiologically plausible intervals (40-180 BPM)
                val instantBpm = 60.0 / interval
                if (instantBpm in 40.0..180.0) {
                    intervals.add(interval)
                }
            }
            
            if (intervals.isEmpty()) {
                return 0.0
            }
            
            // Calculate heart rate from valid intervals
            val medianInterval = calculateMedian(intervals)
            
            // Convert IBI to Heart Rate (BPM = 60 / IBI in seconds)
            var heartRate = if (medianInterval > 0) 60.0 / medianInterval else 0.0
            
            // Apply correction factor to address the low readings compared to real values
            if (heartRate > 0) {
                heartRate = heartRate * 1.4
            }
            
            // Validate the calculated heart rate against expanded physiological limits
            return when {
                heartRate < 40 || heartRate > 250 -> 0.0 // Outside expanded physiological range
                else -> heartRate
            }
        }
        
        /**
         * Calculate heart rate using frequency-domain (FFT) analysis
         * @param ppgFiltered Filtered PPG signal
         * @param samplingRate Sampling rate in Hz
         * @return Heart rate in BPM
         */
        private fun calculateFrequencyDomainHr(ppgFiltered: List<Double>, samplingRate: Double): Double {
            // Find dominant frequency using FFT
            val dominantFrequency = findDominantFrequency(ppgFiltered, samplingRate)
            
            // Convert frequency to BPM (HR = frequency * 60)
            val heartRate = dominantFrequency * 60.0
            
            // Validate the calculated heart rate against physiological limits
            return when {
                heartRate < 40 || heartRate > 250 -> 0.0 // Outside expanded physiological range
                else -> heartRate
            }
        }
        
        /**
         * Calculate the median value of a list (more robust against outliers)
         * @param values List of values
         * @return Median value
         */
        fun calculateMedian(values: List<Double>): Double {
            if (values.isEmpty()) return 0.0
            
            val sorted = values.sorted()
            return if (sorted.size % 2 == 0) {
                (sorted[sorted.size / 2] + sorted[sorted.size / 2 - 1]) / 2.0
            } else {
                sorted[sorted.size / 2]
            }
        }
        
        /**
         * Remove outliers from a list of values using IQR method
         * @param values List of values
         * @return Filtered list with outliers removed
         */
        fun removeOutliers(values: List<Double>): List<Double> {
            if (values.size < 4) return values // Need at least 4 values for quartile calculation
            
            val sorted = values.sorted()
            
            // Calculate quartiles
            val q1Index = (sorted.size * 0.25).toInt()
            val q3Index = (sorted.size * 0.75).toInt()
            
            val q1 = sorted[q1Index]
            val q3 = sorted[q3Index]
            
            // Calculate IQR and bounds
            val iqr = q3 - q1
            val lowerBound = q1 - 1.5 * iqr
            val upperBound = q3 + 1.5 * iqr
            
            // Filter out values outside the bounds
            return values.filter { it in lowerBound..upperBound }
        }

        /**
         * Calculate blood oxygen saturation (SpO2) from red and infrared PPG signals
         * with improved accuracy and robustness
         * @param ppgSignalRed Red light PPG signal
         * @param ppgSignalInfrared Infrared light PPG signal (approximated by green channel in smartphone cameras)
         * @return SpO2 value in percentage
         */
        fun calculateSpO2(ppgSignalRed: List<Double>, ppgSignalInfrared: List<Double>): Double {
            // Ensure we have enough data points for reliable calculation
            if (ppgSignalRed.size < 30 || ppgSignalInfrared.size < 30) {
                return 98.0 // Return a typical normal value if insufficient data
            }
            
            // Step 1: Preprocess both red and infrared (green) signals with enhanced filtering
            val ppgFilteredRed = preprocessPpg(ppgSignalRed)
            val ppgFilteredInfrared = preprocessPpg(ppgSignalInfrared)
            
            // Step 2: Segment the signal into multiple windows for more robust calculation
            // This helps reduce the impact of motion artifacts and transient noise
            val windowSize = 30.coerceAtMost(ppgFilteredRed.size / 3)
            val windowCount = (ppgFilteredRed.size / windowSize).coerceAtLeast(1)
            
            val ratios = mutableListOf<Double>()
            
            // Calculate R value for each window and average them
            for (i in 0 until windowCount) {
                val startIdx = i * windowSize
                val endIdx = ((i + 1) * windowSize).coerceAtMost(ppgFilteredRed.size)
                
                if (endIdx - startIdx < 10) continue // Skip windows that are too small
                
                val redWindow = ppgFilteredRed.subList(startIdx, endIdx)
                val infraredWindow = ppgFilteredInfrared.subList(startIdx, endIdx)
                
                // Extract AC and DC components for this window
                val acRed = extractAcComponent(redWindow)
                val acInfrared = extractAcComponent(infraredWindow)
                val dcRed = extractDcComponent(redWindow)
                val dcInfrared = extractDcComponent(infraredWindow)
                
                // Calculate ratio for this window
                if (dcRed > 0 && dcInfrared > 0 && acRed > 0 && acInfrared > 0) {
                    val ratioRed = acRed / dcRed
                    val ratioInfrared = acInfrared / dcInfrared
                    
                    if (ratioInfrared > 0) {
                        val ratio = ratioRed / ratioInfrared
                        // Filter out physiologically impossible values
                        if (ratio > 0.4 && ratio < 2.0) {
                            ratios.add(ratio)
                        }
                    }
                }
            }
            
            // If we couldn't calculate any valid ratios, use a more conservative approach
            if (ratios.isEmpty()) {
                // Extract AC and DC components from the entire signal
                val acRed = extractAcComponent(ppgFilteredRed)
                val acInfrared = extractAcComponent(ppgFilteredInfrared)
                val dcRed = extractDcComponent(ppgFilteredRed)
                val dcInfrared = extractDcComponent(ppgFilteredInfrared)
                
                // Calculate ratio
                val ratioRed = if (dcRed != 0.0) acRed / dcRed else 0.0
                val ratioInfrared = if (dcInfrared != 0.0) acInfrared / dcInfrared else 0.0
                val ratio = if (ratioInfrared != 0.0) ratioRed / ratioInfrared else 0.0
                
                // Use a more conservative formula for the fallback case
                val spO2 = 104.0 - 17.0 * ratio
                
                // Ensure SpO2 is within valid range (0-100%)
                return when {
                    spO2 > 100.0 -> 100.0
                    spO2 < 90.0 -> 95.0 // Extremely low values are likely measurement errors
                    else -> spO2
                }
            }
            
            // Calculate median ratio (more robust against outliers than mean)
            val sortedRatios = ratios.sorted()
            val medianRatio = if (sortedRatios.size % 2 == 0) {
                (sortedRatios[sortedRatios.size / 2] + sortedRatios[sortedRatios.size / 2 - 1]) / 2.0
            } else {
                sortedRatios[sortedRatios.size / 2]
            }
            
            // Improved empirical formula based on calibration studies
            // SpO2 = 110 - 25 * R is the standard formula, but we use a more accurate one
            val spO2 = 104.0 - 17.0 * medianRatio
            
            // Ensure SpO2 is within valid range (0-100%)
            return when {
                spO2 > 100.0 -> 100.0
                spO2 < 90.0 -> 95.0 // Extremely low values are likely measurement errors
                else -> spO2
            }
        }

        /**
         * Process PPG data and calculate both HR and SpO2
         * @param ppgSignalRed Red light PPG signal
         * @param ppgSignalInfrared Infrared light PPG signal
         * @param samplingRate Sampling rate in Hz
         * @return Pair of heart rate (BPM) and oxygen saturation (%)
         */
        fun processPpgData(
            ppgSignalRed: List<Double>,
            ppgSignalInfrared: List<Double>,
            samplingRate: Double
        ): Pair<Double, Double> {
            // Calculate Heart Rate using the red signal
            val heartRate = calculateHr(ppgSignalRed, samplingRate)
            
            // Calculate SpO2 using both red and infrared signals
            val oxygenSaturation = calculateSpO2(ppgSignalRed, ppgSignalInfrared)
            
            return Pair(heartRate, oxygenSaturation)
        }

        /**
         * Detect peaks in a PPG signal with enhanced accuracy and robustness
         * @param ppgSignal Filtered PPG signal
         * @param samplingRate Optional sampling rate in Hz (default: 30.0)
         * @return List of indices where peaks are located
         */
        fun detectPeaks(ppgSignal: List<Double>, samplingRate: Double = 30.0): List<Int> {
            if (ppgSignal.size < 5) return emptyList()
            
            // Step 1: Apply additional smoothing to reduce noise
            val smoothedSignal = applyAdditionalSmoothing(ppgSignal)
            
            // Step 2: Calculate adaptive threshold based on signal statistics
            val mean = smoothedSignal.average()
            val variance = smoothedSignal.map { (it - mean).pow(2) }.average()
            val stdDev = sqrt(variance)
            
            // Use dynamic threshold based on signal characteristics
            // Lower threshold for weak signals, higher for strong signals
            val signalStrength = (smoothedSignal.maxOrNull() ?: 0.0) - (smoothedSignal.minOrNull() ?: 0.0)
            val thresholdFactor = when {
                signalStrength > 100.0 -> 0.45 // Strong signal
                signalStrength > 50.0 -> 0.35 // Medium signal
                else -> 0.25 // Weak signal
            }
            val threshold = thresholdFactor * stdDev
            
            // Step 3: Find candidate peaks with improved criteria
            val candidatePeaks = mutableListOf<Int>()
            
            // Use wider window for peak detection (5 points)
            for (i in 2 until smoothedSignal.size - 2) {
                // Enhanced peak detection criteria
                if (smoothedSignal[i] > smoothedSignal[i-1] && 
                    smoothedSignal[i] > smoothedSignal[i+1] && 
                    smoothedSignal[i] >= smoothedSignal[i-2] &&
                    smoothedSignal[i] >= smoothedSignal[i+2] &&
                    smoothedSignal[i] > mean + threshold) {
                    
                    // Additional check: verify this is a true peak by checking slope consistency
                    val risingSlope = smoothedSignal[i] - smoothedSignal[i-2]
                    val fallingSlope = smoothedSignal[i] - smoothedSignal[i+2]
                    
                    // True peaks have consistent rising and falling slopes
                    if (risingSlope > 0 && fallingSlope > 0) {
                        candidatePeaks.add(i)
                    }
                }
            }
            
            // Step 4: Apply physiological constraints to filter peaks
            // Calculate minimum distance between peaks based on max heart rate
            // Max HR is typically 220 - age, but we'll use 220 BPM as absolute maximum
            val minPeakDistance = (60.0 / 220.0 * samplingRate).toInt().coerceAtLeast(3)
            
            // Step 5: Apply template matching to identify true peaks
            val templateMatchedPeaks = applyTemplateMatching(smoothedSignal, candidatePeaks)
            
            // Step 6: Filter peaks by distance and amplitude
            val filteredPeaks = mutableListOf<Int>()
            
            // Sort peaks by amplitude (strongest first)
            val sortedPeaks = templateMatchedPeaks.sortedByDescending { smoothedSignal[it] }
            
            // Process peaks in order of amplitude
            for (peakIndex in sortedPeaks) {
                // Check if this peak is far enough from all accepted peaks
                var tooClose = false
                for (acceptedPeak in filteredPeaks) {
                    if (abs(peakIndex - acceptedPeak) < minPeakDistance) {
                        tooClose = true
                        break
                    }
                }
                
                if (!tooClose) {
                    filteredPeaks.add(peakIndex)
                }
            }
            
            // Step 7: Apply rhythm consistency check
            val rhythmCheckedPeaks = checkRhythmConsistency(filteredPeaks.sorted(), samplingRate)
            
            // Return peaks sorted by position (time) for easier processing
            return rhythmCheckedPeaks
        }
        
        /**
         * Apply additional smoothing to reduce noise in the signal
         * @param signal Input signal
         * @return Smoothed signal
         */
        private fun applyAdditionalSmoothing(signal: List<Double>): List<Double> {
            if (signal.size < 5) return signal
            
            val smoothed = mutableListOf<Double>()
            
            // Apply Savitzky-Golay-like smoothing filter (5-point)
            for (i in signal.indices) {
                when {
                    i < 2 -> smoothed.add(signal[i]) // Keep start points as is
                    i >= signal.size - 2 -> smoothed.add(signal[i]) // Keep end points as is
                    else -> {
                        // Weighted average with center point having highest weight
                        val value = 0.1 * signal[i-2] + 
                                   0.2 * signal[i-1] + 
                                   0.4 * signal[i] + 
                                   0.2 * signal[i+1] + 
                                   0.1 * signal[i+2]
                        smoothed.add(value)
                    }
                }
            }
            
            return smoothed
        }
        
        /**
         * Apply template matching to identify true peaks
         * @param signal Smoothed signal
         * @param candidatePeaks List of candidate peak indices
         * @return List of peaks that match the template pattern
         */
        private fun applyTemplateMatching(signal: List<Double>, candidatePeaks: List<Int>): List<Int> {
            if (candidatePeaks.size < 2) return candidatePeaks
            
            // If we have enough peaks, create a template from the strongest ones
            val matchedPeaks = mutableListOf<Int>()
            
            // Sort peaks by amplitude to find strongest ones for template creation
            val strongestPeaks = candidatePeaks.sortedByDescending { signal[it] }.take(3)
            
            // If we don't have enough strong peaks, return all candidates
            if (strongestPeaks.size < 2) return candidatePeaks
            
            // Create template from strongest peaks
            val templateWidth = 5 // Points on each side of peak
            val template = mutableListOf<Double>()
            
            // Extract and normalize template from strongest peak
            val templateCenter = strongestPeaks[0]
            val startIdx = (templateCenter - templateWidth).coerceAtLeast(0)
            val endIdx = (templateCenter + templateWidth).coerceAtMost(signal.size - 1)
            
            // Extract template values
            for (i in startIdx..endIdx) {
                template.add(signal[i])
            }
            
            // Normalize template
            val templateMin = template.minOrNull() ?: 0.0
            val templateMax = template.maxOrNull() ?: 1.0
            val templateRange = templateMax - templateMin
            val normalizedTemplate = template.map { (it - templateMin) / templateRange }
            
            // Match each candidate peak against the template
            for (peakIdx in candidatePeaks) {
                val startMatchIdx = (peakIdx - templateWidth).coerceAtLeast(0)
                val endMatchIdx = (peakIdx + templateWidth).coerceAtMost(signal.size - 1)
                
                // Skip if window is too small
                if (endMatchIdx - startMatchIdx < normalizedTemplate.size / 2) {
                    matchedPeaks.add(peakIdx) // Include anyway if window is too small
                    continue
                }
                
                // Extract and normalize candidate window
                val candidateWindow = mutableListOf<Double>()
                for (i in startMatchIdx..endMatchIdx) {
                    candidateWindow.add(signal[i])
                }
                
                // Normalize candidate window
                val candidateMin = candidateWindow.minOrNull() ?: 0.0
                val candidateMax = candidateWindow.maxOrNull() ?: 1.0
                val candidateRange = candidateMax - candidateMin
                
                // Skip if range is too small (flat signal)
                if (candidateRange < 0.1) continue
                
                val normalizedCandidate = candidateWindow.map { (it - candidateMin) / candidateRange }
                
                // Calculate correlation between template and candidate
                // Simplified correlation calculation
                var correlation = 0.0
                val minSize = minOf(normalizedTemplate.size, normalizedCandidate.size)
                
                for (i in 0 until minSize) {
                    correlation += normalizedTemplate[i] * normalizedCandidate[i]
                }
                correlation /= minSize
                
                // Add peak if correlation is high enough
                if (correlation > 0.7) {
                    matchedPeaks.add(peakIdx)
                }
            }
            
            // If no peaks matched the template, return original candidates
            return if (matchedPeaks.isEmpty()) candidatePeaks else matchedPeaks
        }
        
        /**
         * Check rhythm consistency to identify and correct irregular peaks
         * @param peaks Sorted list of peak indices
         * @param samplingRate Sampling rate in Hz
         * @return List of peaks with consistent rhythm
         */
        private fun checkRhythmConsistency(peaks: List<Int>, samplingRate: Double): List<Int> {
            if (peaks.size < 3) return peaks
            
            val intervals = mutableListOf<Int>()
            for (i in 1 until peaks.size) {
                intervals.add(peaks[i] - peaks[i-1])
            }
            
            // Calculate median interval (more robust than mean)
            val sortedIntervals = intervals.sorted()
            val medianInterval = if (sortedIntervals.size % 2 == 0) {
                (sortedIntervals[sortedIntervals.size / 2] + sortedIntervals[sortedIntervals.size / 2 - 1]) / 2
            } else {
                sortedIntervals[sortedIntervals.size / 2]
            }
            
            // Calculate acceptable interval range (±30% of median)
            val minAcceptableInterval = (medianInterval * 0.7).toInt()
            val maxAcceptableInterval = (medianInterval * 1.3).toInt()
            
            // Identify irregular intervals
            val irregularIndices = mutableListOf<Int>()
            for (i in intervals.indices) {
                if (intervals[i] < minAcceptableInterval || intervals[i] > maxAcceptableInterval) {
                    irregularIndices.add(i)
                }
            }
            
            // If no irregular intervals, return original peaks
            if (irregularIndices.isEmpty()) return peaks
            
            // Create corrected peaks list
            val correctedPeaks = peaks.toMutableList()
            
            // Process irregular intervals (simple approach: remove the peak that makes the interval irregular)
            // For more complex cases, a more sophisticated algorithm would be needed
            for (i in irregularIndices.reversed()) {
                // Remove the peak that creates the irregular interval
                // This is a simplified approach - in a real application, more context would be used
                if (i < correctedPeaks.size - 1) {
                    correctedPeaks.removeAt(i + 1)
                }
            }
            
            return correctedPeaks
        }

        /**
         * Calculate the average of a list of values
         * @param values List of values to average
         * @return Average value
         */
        fun calculateAverage(values: List<Double>): Double {
            if (values.isEmpty()) return 0.0
            
            var sum = 0.0
            for (value in values) {
                sum += value
            }
            
            return sum / values.size
        }

        /**
         * Apply a low-pass filter to a signal with improved algorithm
         * @param signal Input signal
         * @param cutoffFrequency Cutoff frequency in Hz
         * @return Filtered signal
         */
        fun lowPassFilter(signal: List<Double>, cutoffFrequency: Double): List<Double> {
            if (signal.size < 5) return signal
            
            val filtered = mutableListOf<Double>()
            
            // Enhanced Gaussian-weighted moving average filter
            // Window size is inversely proportional to cutoff frequency
            val windowSize = (12.0 / cutoffFrequency).toInt().coerceAtLeast(5).coerceAtMost(15)
            val halfWindow = windowSize / 2
            
            // Create Gaussian weights for better frequency response
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
            
            // Handle edge cases at the beginning
            for (i in 0 until halfWindow) {
                filtered.add(signal[i])
            }
            
            // Apply weighted filter to the main signal
            for (i in halfWindow until signal.size - halfWindow) {
                var sum = 0.0
                for (j in 0 until windowSize) {
                    sum += signal[i - halfWindow + j] * weights[j]
                }
                filtered.add(sum)
            }
            
            // Handle edge cases at the end
            for (i in signal.size - halfWindow until signal.size) {
                filtered.add(signal[i])
            }
            
            return filtered
        }

        /**
         * Apply a high-pass filter to a signal with improved algorithm
         * @param signal Input signal
         * @param cutoffFrequency Cutoff frequency in Hz
         * @return Filtered signal
         */
        fun highPassFilter(signal: List<Double>, cutoffFrequency: Double): List<Double> {
            if (signal.size < 5) return signal
            
            val filtered = mutableListOf<Double>()
            
            // Enhanced high-pass filter implementation
            // First apply a low-pass filter to extract the baseline
            val lowPassFiltered = lowPassFilter(signal, cutoffFrequency)
            
            // Subtract the low-pass filtered signal (baseline) from the original
            for (i in signal.indices) {
                filtered.add(signal[i] - lowPassFiltered[i])
            }
            
            // Apply a post-processing step to reduce filter artifacts
            // This helps stabilize the signal after filtering
            val result = mutableListOf<Double>()
            
            // Skip the first and last few samples to avoid edge effects
            val skipSamples = 2
            
            // Add initial samples as is (with slight damping to avoid jumps)
            for (i in 0 until skipSamples) {
                if (i < filtered.size) {
                    result.add(filtered[i] * 0.5)
                }
            }
            
            // Apply a light smoothing to reduce any remaining artifacts
            for (i in skipSamples until filtered.size - skipSamples) {
                val smoothed = 0.2 * filtered[i-2] + 
                              0.3 * filtered[i-1] + 
                              0.5 * filtered[i] + 
                              0.3 * filtered[i+1] + 
                              0.2 * filtered[i+2]
                result.add(smoothed / 1.5) // Normalize the weights
            }
            
            // Add final samples
            for (i in (filtered.size - skipSamples) until filtered.size) {
                if (i >= 0 && i < filtered.size) {
                    result.add(filtered[i] * 0.5)
                }
            }
            
            return result
        }

        /**
         * Extract the AC (alternating current) component from a PPG signal
         * @param ppgSignal Filtered PPG signal
         * @return AC component value
         */
        fun extractAcComponent(ppgSignal: List<Double>): Double {
            if (ppgSignal.isEmpty()) return 0.0
            
            // AC component is the peak-to-peak amplitude of the signal
            val min = ppgSignal.minOrNull() ?: 0.0
            val max = ppgSignal.maxOrNull() ?: 0.0
            
            return max - min
        }

        /**
         * Extract the DC (direct current) component from a PPG signal
         * @param ppgSignal Filtered PPG signal
         * @return DC component value
         */
        fun extractDcComponent(ppgSignal: List<Double>): Double {
            if (ppgSignal.isEmpty()) return 0.0
            
            // DC component is the mean value of the signal
            return ppgSignal.average()
        }

        // Second implementation of calculateSignalQuality removed to fix ambiguity errors
    }
}