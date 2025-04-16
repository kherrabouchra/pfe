package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.HeartRateResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class VitalsViewModel : ViewModel() {
    // Heart rate measurement data
    private val _lastHeartRate = MutableStateFlow<HeartRateResult?>(null)
    val lastHeartRate: StateFlow<HeartRateResult?> = _lastHeartRate.asStateFlow()

    private val _heartRateHistory = MutableStateFlow<List<HeartRateResult>>(emptyList())
    val heartRateHistory: StateFlow<List<HeartRateResult>> = _heartRateHistory.asStateFlow()

    // Function to store heart rate measurement
    fun setLastHeartRate(heartRate: Int) {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
        val timestamp = dateFormat.format(Date())
        
        val status = when {
            heartRate < 60 -> "Low"
            heartRate > 100 -> "High"
            else -> "Normal"
        }
        
        val heartRateResult = HeartRateResult(
            heartRate = heartRate,
            status = status,
            timestamp = timestamp,
            confidenceLevel = "High" // Default confidence level
        )

        _lastHeartRate.value = heartRateResult
        
        // Add to history
        val currentHistory = _heartRateHistory.value.toMutableList()
        currentHistory.add(heartRateResult)
        _heartRateHistory.value = currentHistory
    }

    // Function to clear heart rate history
    fun clearHeartRateHistory() {
        _heartRateHistory.value = emptyList()
        _lastHeartRate.value = null
    }
}