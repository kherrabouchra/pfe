package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FallDetectionViewModel : ViewModel() {
    private val _fallAlertText = MutableStateFlow("")
    val fallAlertText: StateFlow<String> = _fallAlertText.asStateFlow()

    fun updateFallAlert(text: String) {
        _fallAlertText.value = text
    }

    fun clearFallAlert() {
        _fallAlertText.value = ""
    }
} 