package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainViewModel : ViewModel() {

    // Example state variable
    private var _userLoggedIn = false
    val userLoggedIn: Boolean
        get() = _userLoggedIn

    // Heart rate measurement data
    private val _lastHeartRate = MutableStateFlow<HeartRateData?>(null)
    val lastHeartRate: StateFlow<HeartRateData?> = _lastHeartRate.asStateFlow()

    // Example function to update login status
    fun setUserLoggedIn(isLoggedIn: Boolean) {
        _userLoggedIn = isLoggedIn
    }

    // Function to store heart rate measurement
    fun setLastHeartRate(heartRate: Int) {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
        val timestamp = dateFormat.format(Date())
        
        val status = when {
            heartRate < 60 -> "Low"
            heartRate > 100 -> "High"
            else -> "Normal"
        }
        
        _lastHeartRate.value = HeartRateData(heartRate, status, timestamp)
    }

    // Example function for any initialization logic
    fun initializeApp() {
        viewModelScope.launch {
            // Load user settings, check authentication, etc.
        }
    }
    
    // Data class to hold heart rate information
    data class HeartRateData(
        val heartRate: Int,
        val status: String,
        val timestamp: String
    )
}
