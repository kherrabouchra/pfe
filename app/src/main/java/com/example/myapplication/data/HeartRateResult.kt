package com.example.myapplication.data

data class HeartRateResult(
    val heartRate: Int,
    val status: String,
    val timestamp: String,
    val confidenceLevel: String = "Medium" // Confidence level of the measurement (Low, Medium, High)
)