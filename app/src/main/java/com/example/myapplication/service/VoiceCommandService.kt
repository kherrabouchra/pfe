
package com.example.myapplication.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.PowerManager
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import java.util.Locale

class VoiceCommandService : Service(), RecognitionListener {
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var recognizerIntent: Intent
    private lateinit var wakeLock: PowerManager.WakeLock
    private val handler = Handler(Looper.getMainLooper())
    private var isListening = false
    
    // Emergency keywords to listen for
    private val emergencyKeywords = listOf("help", "emergency", "sos", "danger")
    
    // Call-related keywords
    private val callKeywords = listOf("call", "phone", "dial")
    
    // Emergency phone number to call
    private val emergencyPhoneNumber = "123456789" // Replace with actual emergency number
    
    // Emergency contacts list - should be synchronized with the contacts in FallDetectionService
    private var emergencyContacts: List<String> = listOf(
        // Add your emergency contact numbers here if needed
        // Example: "+1234567890"
    )
    
    companion object {
        private const val TAG = "VoiceCommandService"
        private const val NOTIFICATION_CHANNEL_ID = "voice_command_channel"
        private const val NOTIFICATION_ID = 2
        private const val RESTART_DELAY_MS = 1000L // 1 second delay before restarting recognition
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service onCreate() called")
        
        setupWakeLock()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification("Voice Command Detection Active", "Listening for emergency commands"))
        
        initializeSpeechRecognizer()
        Log.d(TAG, "Service started in continuous monitoring mode")
        
        Log.d(TAG, "Service started in foreground")
    }
    
    private fun setupWakeLock() {
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "VoiceCommand::WakeLock"
        )
    }
    
    private lateinit var audioManager: AudioManager
    private var audioFocusRequest: AudioManager.OnAudioFocusChangeListener? = null
    private var hasAudioFocus = false
    
    private fun initializeSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            // Initialize AudioManager
            audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            
            // Create speech recognizer
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
            speechRecognizer.setRecognitionListener(this)
            
            recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                // Continuous recognition
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 500)
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 500)
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 15000)
            }
            
            startListening()
        } else {
            Log.e(TAG, "Speech recognition not available on this device")
        }
    }
    
    private fun startListening() {
        if (!isListening && this::speechRecognizer.isInitialized) {
            try {
                if (!wakeLock.isHeld) {
                    wakeLock.acquire(10*60*1000L) // 10 minutes timeout
                    Log.d(TAG, "WakeLock acquired: ${wakeLock.isHeld}")
                }
                
                // Request audio focus before starting speech recognition
                if (requestAudioFocus()) {
                    speechRecognizer.startListening(recognizerIntent)
                    isListening = true
                    Log.d(TAG, "Started listening for voice commands")
                } else {
                    Log.e(TAG, "Failed to get audio focus, cannot start speech recognition")
                    // Try again after a delay
                    handler.postDelayed({
                        startListening()
                    }, RESTART_DELAY_MS * 2) // Longer delay for audio focus retry
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error starting speech recognition", e)
                restartListening()
            }
        }
    }
    
    private fun stopListening() {
        if (isListening && this::speechRecognizer.isInitialized) {
            try {
                speechRecognizer.stopListening()
                isListening = false
                Log.d(TAG, "Stopped listening for voice commands")
                
                // Release audio focus when stopping speech recognition
                releaseAudioFocus()
            } catch (e: Exception) {
                Log.e(TAG, "Error stopping speech recognition", e)
            }
        }
    }
    
    private fun restartListening() {
        stopListening()
        handler.postDelayed({
            startListening()
        }, RESTART_DELAY_MS)
    }
    
    private fun processVoiceResults(results: List<String>) {
        Log.d(TAG, "Processing voice results: $results")
        
        // Show notification for voice input detection
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(
            NOTIFICATION_ID,
            createNotification(
                "Voice Input Detected",
                "Detected: ${results.firstOrNull() ?: ""}"
            )
        )
        
        // Check if any of the recognized phrases contain emergency keywords
        for (result in results) {
            val lowerResult = result.lowercase(Locale.getDefault())
            
            // Check for emergency keywords
            for (keyword in emergencyKeywords) {
                if (lowerResult.contains(keyword)) {
                    Log.i(TAG, "Emergency keyword detected: $keyword in '$result'")
                    triggerEmergencyCall()
                    return
                }
            }
            
            // Check for call-related keywords
            for (keyword in callKeywords) {
                if (lowerResult.contains(keyword)) {
                    Log.i(TAG, "Call keyword detected: $keyword in '$result'")
                    // Extract potential contact name or number after the call keyword
                    val callIndex = lowerResult.indexOf(keyword)
                    if (callIndex >= 0 && callIndex + keyword.length < lowerResult.length) {
                        val potentialContact = lowerResult.substring(callIndex + keyword.length).trim()
                        if (potentialContact.isNotEmpty()) {
                            handleCallCommand(potentialContact)
                            return
                        }
                    }
                    // If no specific contact mentioned, use default emergency number
                    triggerEmergencyCall()
                    return
                }
            }
        }
    }
    // Handle call command with potential contact name or number
    private fun handleCallCommand(potentialContact: String) {
        Log.i(TAG, "Handling call command for: $potentialContact")
        
        // First check if it's a direct phone number (simple check for digits)
        if (potentialContact.all { it.isDigit() || it == '+' || it == '-' || it == ' ' || it == '(' || it == ')' }) {
            // Clean up the number
            val cleanNumber = potentialContact.filter { it.isDigit() || it == '+' }
            if (cleanNumber.length >= 7) { // Minimum length for a valid phone number
                makePhoneCall(cleanNumber)
                return
            }
        }
        
        // Check if it matches any emergency contact name (simplified implementation)
        // In a real app, you would query contacts database
        if (potentialContact.contains("emergency") || 
            potentialContact.contains("help") || 
            potentialContact.contains("sos")) {
            triggerEmergencyCall()
            return
        }
        
        // If no match found, use the default emergency number
        Log.i(TAG, "No specific contact matched, using default emergency number")
        triggerEmergencyCall()
    }
    
    private fun makePhoneCall(phoneNumber: String) {
        Log.i(TAG, "Making phone call to $phoneNumber")
        
        // Check for phone call permission
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) 
                == PackageManager.PERMISSION_GRANTED) {
            try {
                // Create intent to make a phone call
                val callIntent = Intent(Intent.ACTION_CALL).apply {
                    data = Uri.parse("tel:$phoneNumber")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                startActivity(callIntent)
                
                // Update notification to show call is in progress
                val notificationManager = 
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(
                    NOTIFICATION_ID,
                    createNotification(
                        "Call Initiated", 
                        "Calling $phoneNumber"
                    )
                )
            } catch (e: Exception) {
                Log.e(TAG, "Failed to initiate phone call", e)
                showCallFailureNotification("Failed to make call: ${e.message}")
            }
        } else {
            Log.e(TAG, "Cannot make phone call - CALL_PHONE permission not granted")
            showCallFailureNotification("Call permission not granted")
        }
    }
    
    private fun triggerEmergencyCall() {
        Log.i(TAG, "Triggering emergency call to $emergencyPhoneNumber")
        
        // Check for phone call permission
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) 
                == PackageManager.PERMISSION_GRANTED) {
            try {
                // Create intent to make a phone call
                val callIntent = Intent(Intent.ACTION_CALL).apply {
                    data = Uri.parse("tel:$emergencyPhoneNumber")
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                startActivity(callIntent)
                
                // Update notification to show emergency call is in progress
                val notificationManager = 
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(
                    NOTIFICATION_ID, 
                    createNotification(
                        "Emergency Call Triggered", 
                        "Calling $emergencyPhoneNumber"
                    )
                )
            } catch (e: Exception) {
                Log.e(TAG, "Failed to initiate emergency call", e)
                showCallFailureNotification("Failed to make emergency call: ${e.message}")
            }
        } else {
            Log.e(TAG, "Cannot make emergency call - CALL_PHONE permission not granted")
            showCallFailureNotification("Call permission not granted")
        }
    }
    
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val name = "Voice Command Detection"
        val descriptionText = "Monitors for emergency voice commands"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            name,
            importance
        ).apply {
            description = descriptionText
        }

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
    
    private fun createNotification(
        title: String,
        text: String,
        pendingIntent: PendingIntent? = null
    ): Notification {
        val defaultPendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val builder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(R.drawable.better_logo)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent ?: defaultPendingIntent)
        
        return builder.build()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand() called")
        if (!isListening) {
            startListening()
        }
        return START_STICKY
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Service onDestroy() called")
        
        if (this::speechRecognizer.isInitialized) {
            stopListening()
            speechRecognizer.destroy()
        }
        
        // Make sure to release audio focus when service is destroyed
        releaseAudioFocus()
        
        if (wakeLock.isHeld) {
            wakeLock.release()
            Log.d(TAG, "WakeLock released")
        }
    }
    
    // Show notification for call failures
    private fun showCallFailureNotification(message: String) {
        val notificationManager = 
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(
            NOTIFICATION_ID + 1,
            createNotification(
                "Call Failed", 
                message
            )
        )
    }
    
    // RecognitionListener implementation
    override fun onReadyForSpeech(params: Bundle?) {
        Log.d(TAG, "Ready for speech")
    }

    override fun onBeginningOfSpeech() {
        Log.d(TAG, "Beginning of speech")
    }

    override fun onRmsChanged(rmsdB: Float) {
        // Not logging RMS changes to avoid log spam
    }

    override fun onBufferReceived(buffer: ByteArray?) {
        Log.d(TAG, "Buffer received")
    }

    override fun onEndOfSpeech() {
        Log.d(TAG, "End of speech")
    }

    override fun onError(error: Int) {
        val errorMessage = when (error) {
            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
            SpeechRecognizer.ERROR_CLIENT -> "Client side error"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
            SpeechRecognizer.ERROR_NETWORK -> "Network error"
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
            SpeechRecognizer.ERROR_NO_MATCH -> "No match found"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "RecognitionService busy"
            SpeechRecognizer.ERROR_SERVER -> "Server error"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
            else -> "Unknown error"
        }
        Log.e(TAG, "Speech recognition error: $errorMessage")
        
        // Only restart listening for non-fatal errors
        when (error) {
            SpeechRecognizer.ERROR_NO_MATCH,
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> {
                restartListening()
            }
            else -> {
                // For other errors, wait longer before retrying
                handler.postDelayed({
                    restartListening()
                }, RESTART_DELAY_MS * 5)
            }
        }
    }

    override fun onResults(results: Bundle?) {
        Log.d(TAG, "Speech recognition results received")
        results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.let { voiceResults ->
            if (voiceResults.isNotEmpty()) {
                processVoiceResults(voiceResults)
            }
        }
        
        // Restart listening to continue monitoring
        restartListening()
    }

    override fun onPartialResults(partialResults: Bundle?) {
        Log.d(TAG, "Partial speech recognition results received")
        partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.let { voiceResults ->
            if (voiceResults.isNotEmpty()) {
                processVoiceResults(voiceResults)
            }
        }
    }

    override fun onEvent(eventType: Int, params: Bundle?) {
        Log.d(TAG, "Speech recognition event: $eventType")
    }
    
    private fun requestAudioFocus(): Boolean {
        if (hasAudioFocus) return true
        
        audioFocusRequest = AudioManager.OnAudioFocusChangeListener { focusChange ->
            when (focusChange) {
                AudioManager.AUDIOFOCUS_LOSS -> {
                    // Only stop listening on complete loss, not on transient loss
                    stopListening()
                    hasAudioFocus = false
                }
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                    // For transient loss, we'll keep the flag but pause listening
                    // This prevents the service from stopping completely
                    if (isListening) {
                        speechRecognizer.stopListening()
                        isListening = false
                    }
                    // Don't set hasAudioFocus to false here
                }
                AudioManager.AUDIOFOCUS_GAIN -> {
                    if (!isListening) startListening()
                    hasAudioFocus = true
                }
            }
        }
        
        val result = audioManager.requestAudioFocus(
            audioFocusRequest,
            AudioManager.STREAM_VOICE_CALL,
            AudioManager.AUDIOFOCUS_GAIN
        )
        
        hasAudioFocus = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        return hasAudioFocus
    }
    
    private fun releaseAudioFocus() {
        if (!hasAudioFocus) return
        
        audioFocusRequest?.let { listener ->
            audioManager.abandonAudioFocus(listener)
            hasAudioFocus = false
            audioFocusRequest = null
        }
    }
}