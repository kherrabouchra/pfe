package com.example.myapplication.data

/**
 * Combined data class to hold both heart rate and SpO2 measurement results
 */
data class VitalSignsResult(
    val heartRate: Int, // Heart rate in BPM
    val oxygenSaturation: Int, // SpO2 value in percentage
    val heartRateStatus: String, // Status description (Low, Normal, High)
    val oxygenStatus: String, // Status description (Low, Normal, High)
    val timestamp: String, // Timestamp of the measurement
    val confidenceLevel: String = "Medium" // Confidence level of the measurement (Low, Medium, High)
)