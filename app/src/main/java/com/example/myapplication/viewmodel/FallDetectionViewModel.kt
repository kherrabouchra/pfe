package com.example.myapplication.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.service.FallDetectionService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FallDetectionViewModel(application: Application) : AndroidViewModel(application) {
    private val _isDetectionEnabled = MutableStateFlow(true)
    val isDetectionEnabled: StateFlow<Boolean> = _isDetectionEnabled

    private val _fallAlertText = MutableStateFlow("")
    val fallAlertText: StateFlow<String> = _fallAlertText

    private val _showConfirmationDialog = MutableStateFlow(false)
    val showConfirmationDialog: StateFlow<Boolean> = _showConfirmationDialog
    
    private val _countdownSeconds = MutableStateFlow(30)
    val countdownSeconds: StateFlow<Int> = _countdownSeconds
    
    private val _isCountdownActive = MutableStateFlow(false)
    val isCountdownActive: StateFlow<Boolean> = _isCountdownActive
    
    private var emergencyCountdownTimer: CountDownTimer? = null
    
    init {
        // Start the fall detection service when the ViewModel is created
        if (_isDetectionEnabled.value) {
            startFallDetectionService()
        }
    }
    
    fun enableFallDetection(enabled: Boolean) {
        _isDetectionEnabled.value = enabled
        
        if (enabled) {
            startFallDetectionService()
        } else {
            stopFallDetectionService()
        }
    }

    fun onFallDetected() {
        _fallAlertText.value = "Possible fall detected!"
        _showConfirmationDialog.value = true
        startEmergencyCountdown()
    }

    fun confirmPhoneFall() {
        _fallAlertText.value = ""
        _showConfirmationDialog.value = false
        cancelEmergencyCountdown()
    }

    fun markAsEmergency() {
        _fallAlertText.value = "Emergency! Unconfirmed fall."
        _showConfirmationDialog.value = false
        cancelEmergencyCountdown()
        triggerEmergencyAction()
    }

    fun resetFallAlert() {
        _fallAlertText.value = ""
        _showConfirmationDialog.value = false
        cancelEmergencyCountdown()
    }
    
    private fun startEmergencyCountdown() {
        _isCountdownActive.value = true
        _countdownSeconds.value = 30
        
        emergencyCountdownTimer = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _countdownSeconds.value = (millisUntilFinished / 1000).toInt()
            }

            override fun onFinish() {
                _isCountdownActive.value = false
                // If the countdown finishes without user confirmation, trigger emergency
                markAsEmergency()
            }
        }.start()
    }
    
    private fun cancelEmergencyCountdown() {
        emergencyCountdownTimer?.cancel()
        _isCountdownActive.value = false
    }
    
    private fun triggerEmergencyAction() {
        // TODO: Implement emergency actions like calling emergency contacts
        viewModelScope.launch {
            // This would be where you'd implement emergency contact notifications
            // For now, we'll just update the UI
            _fallAlertText.value = "Emergency services would be notified here"
        }
    }
    
    private fun startFallDetectionService() {
        val context = getApplication<Application>().applicationContext
        val intent = Intent(context, FallDetectionService::class.java)
        context.startService(intent)
    }
    
    private fun stopFallDetectionService() {
        val context = getApplication<Application>().applicationContext
        val intent = Intent(context, FallDetectionService::class.java)
        context.stopService(intent)
    }
    
    override fun onCleared() {
        super.onCleared()
        cancelEmergencyCountdown()
    }
}
