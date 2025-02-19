package com.example.myapplication.viewmodel

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.myapplication.service.FallDetectionService

data class UserProfile(
    val name: String = "",
    val email: String = ""
)

class MainViewModel : ViewModel() {
    private val _fallAlertText = MutableStateFlow("")
    val fallAlertText: StateFlow<String> = _fallAlertText

    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile

    fun updateFallAlertText(text: String) {
        _fallAlertText.value = text
    }

    fun updateUserProfile(name: String, email: String) {
        _userProfile.value = UserProfile(name, email)
    }

    fun startFallDetectionService(context: Context) {
        val serviceIntent = Intent(context, FallDetectionService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }

    fun stopFallDetectionService(context: Context) {
        context.stopService(Intent(context, FallDetectionService::class.java))
    }
} 