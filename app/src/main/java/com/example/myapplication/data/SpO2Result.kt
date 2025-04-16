package com.example.myapplication.data

data class SpO2Result(
    val oxygenSaturation: Int, // SpO2 value in percentage
    val status: String, // Status description (Low, Normal, High)
    val timestamp: String, // Timestamp of the measurement
    val confidenceLevel: String = "Medium" // Confidence level of the measurement (Low, Medium, High)
)