package com.example.myapplication

import android.os.Build
import android.os.Bundle
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplication.navigation.AppNavigation
import com.example.myapplication.ui.theme.BetterAppTheme
import com.example.myapplication.viewmodel.FallDetectionViewModel
import com.example.myapplication.viewmodel.MainViewModel
import android.widget.Toast
import android.content.Intent
import com.example.myapplication.navigation.Screen

class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()
    private val fallDetectionViewModel: FallDetectionViewModel by viewModels()

    private val REQUEST_CODE_NOTIFICATION_PERMISSION = 1001
    private val REQUEST_CODE_AUDIO_PERMISSION = 1002
    private val REQUEST_CODE_CALL_PHONE_PERMISSION = 1003

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if the app was launched from a fall detection notification
        val showFallScreen = checkForFallDetectionIntent(intent)

        // Check and request notification permission if needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // Request the permission
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), REQUEST_CODE_NOTIFICATION_PERMISSION)
            }
        }
        
        // Check and request audio recording permission
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // Request the permission
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.RECORD_AUDIO), REQUEST_CODE_AUDIO_PERMISSION)
        } else {
            // Permission already granted, start voice command service
            startVoiceCommandService()
        }
        
        // Check and request call phone permission for emergency calls
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // Request the permission
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CALL_PHONE), REQUEST_CODE_CALL_PHONE_PERMISSION)
        }


        setContent {
            BetterAppTheme {
                AppNavigation(
                    mainViewModel = mainViewModel,
                    fallDetectionViewModel = fallDetectionViewModel,
                    startDestination = if (showFallScreen) Screen.Fall.route else Screen.Splash.route
                )
            }
        }
    }

    // Handle new intents when app is already running
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Update the activity's intent
        setIntent(intent)
        // Check for fall detection intent and navigate if needed
        if (checkForFallDetectionIntent(intent)) {
            // Recreate the activity to apply the new start destination
            recreate()
        }
    }

    // Helper method to check for fall detection intent
    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkForFallDetectionIntent(intent: Intent?): Boolean {
        if (intent?.getBooleanExtra("SHOW_FALL_CONFIRMATION", false) == true) {
            // Trigger the fall detection alert in the ViewModel
            fallDetectionViewModel.onFallDetected()
            return true
        }
        return false
    }

    // Handle the result of the permission request
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_CODE_NOTIFICATION_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, proceed with notifications
                    Toast.makeText(this, "Notification Permission Granted", Toast.LENGTH_SHORT).show()
                } else {
                    // Permission denied, notify the user
                    Toast.makeText(this, "Notification Permission Denied", Toast.LENGTH_SHORT).show()
                }
            }
            REQUEST_CODE_AUDIO_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, start voice command service
                    Toast.makeText(this, "Audio Recording Permission Granted", Toast.LENGTH_SHORT).show()
                    startVoiceCommandService()
                } else {
                    // Permission denied, notify the user
                    Toast.makeText(this, "Audio Recording Permission Denied - Voice Commands Disabled", Toast.LENGTH_SHORT).show()
                }
            }
            REQUEST_CODE_CALL_PHONE_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted for emergency calls
                    Toast.makeText(this, "Phone Call Permission Granted", Toast.LENGTH_SHORT).show()
                } else {
                    // Permission denied, notify the user
                    Toast.makeText(this, "Phone Call Permission Denied - Emergency Calls Disabled", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    // Helper method to start voice command service
    private fun startVoiceCommandService() {
        val voiceCommandIntent = Intent(this, com.example.myapplication.service.VoiceCommandService::class.java)
        startService(voiceCommandIntent)
    }
    
    companion object {
        // Firebase connection test
        fun testFirebaseConnection() {
            FirebaseTest.testConnection()
        }
    }
}
