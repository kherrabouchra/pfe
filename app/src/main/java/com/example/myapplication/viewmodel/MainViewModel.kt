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

    // Example function to update login status
    fun setUserLoggedIn(isLoggedIn: Boolean) {
        _userLoggedIn = isLoggedIn
    }

    // Example function for any initialization logic
    fun initializeApp() {
        viewModelScope.launch {
            // Load user settings, check authentication, etc.
        }
    }
    

}
