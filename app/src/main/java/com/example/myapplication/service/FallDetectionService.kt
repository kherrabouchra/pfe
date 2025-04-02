package com.example.myapplication.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.telephony.SmsManager
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlin.math.sqrt

class FallDetectionService : Service(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor
    private lateinit var wakeLock: PowerManager.WakeLock
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var isRunning = false
    private var lastLocation: Location? = null
    // Removed Firebase Firestore dependency
    // Using hardcoded emergency contacts instead of Firebase
    private var emergencyContacts: List<String> = listOf(
        // Add your emergency contact numbers here if needed
        // Example: "+1234567890"
    )

    // Fall detection constants - simplified for shake detection
    companion object {
        private const val IMPACT_THRESHOLD = 29f       // Significantly reduced threshold for easier shake detection
        private const val NOTIFICATION_CHANNEL_ID = "fall_detection_channel"
        private const val NOTIFICATION_ID = 1
        private const val TAG = "FallDetectionService"  // Tag for logging
    }

    // Fall detection variables - simplified for shake detection
    private var lastShakeTime = 0L

    @SuppressLint("ForegroundServiceType")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service onCreate() called")
        setupSensors()
        setupWakeLock()
        setupLocationClient()
        // Removed setupFirestore() call to avoid Firebase initialization issues
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification("Fall Detection Active", "Monitoring for falls"))
        Log.d(TAG, "Service started in foreground")
    }

    private fun setupSensors() {
        Log.d(TAG, "Setting up sensors")
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            ?: throw IllegalStateException("Accelerometer not available")
        Log.d(TAG, "Accelerometer sensor acquired: ${accelerometer.name}")
    }

    private fun setupWakeLock() {
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "FallDetection::WakeLock"
        )
        // Don't acquire wake lock here, we'll do it in startMonitoring()
    }
    
    private fun setupLocationClient() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        requestLocationUpdates()
    }
    
    // Removed Firebase Firestore methods
    // If you need to add emergency contacts, add them to the emergencyContacts list in the class declaration

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates() {
        try {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token)
                .addOnSuccessListener { location ->
                    if (location != null) {
                        lastLocation = location
                        Log.d(TAG, "Updated location: ${location.latitude}, ${location.longitude}")
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "Error requesting location updates", e)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand() called with intent: ${intent?.action}, isRunning: $isRunning")
        try {
            if (!isRunning) {
                startMonitoring()
                isRunning = true
                Log.d(TAG, "Monitoring started successfully")
            } else {
                Log.d(TAG, "Service already running, ignoring start command")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error starting monitoring", e)
            // Ensure service doesn't crash even if there's an exception
        }
        return START_STICKY
    }

    private fun startMonitoring() {
        Log.d(TAG, "Starting monitoring")
        wakeLock.acquire(10*60*1000L) // 10 minutes timeout
        Log.d(TAG, "WakeLock acquired: ${wakeLock.isHeld}")
        sensorManager.registerListener(
            this,
            accelerometer,
            SensorManager.SENSOR_DELAY_NORMAL
        )
        Log.d(TAG, "Sensor listener registered with delay: ${SensorManager.SENSOR_DELAY_NORMAL}")
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val ax = event.values[0]
            val ay = event.values[1]
            val az = event.values[2]

            // Calculate acceleration magnitude
            val accelerationMagnitude = sqrt(ax * ax + ay * ay + az * az)
            
            // Log every 50th reading to avoid flooding the log
            if (System.currentTimeMillis() % 50 == 0L) {
                Log.v(TAG, "Acceleration: x=$ax, y=$ay, z=$az, magnitude=$accelerationMagnitude")
            }
            
            // Log when acceleration is close to threshold for debugging
            if (accelerationMagnitude > IMPACT_THRESHOLD * 0.7) {
                Log.d(TAG, "Significant acceleration detected: $accelerationMagnitude (threshold: $IMPACT_THRESHOLD)")
            }

            // Simplified detection: just check for acceleration above threshold
            // with a cooldown period to avoid multiple triggers
            val currentTime = System.currentTimeMillis()
            if (accelerationMagnitude > IMPACT_THRESHOLD) {
                Log.i(TAG, "THRESHOLD EXCEEDED: $accelerationMagnitude > $IMPACT_THRESHOLD")
                
                if (currentTime - lastShakeTime > 3000) {
                    // Update location before triggering alert
                    updateLocationAndTriggerFallAlert()
                    lastShakeTime = currentTime
                } else {
                    Log.d(TAG, "Within cooldown period (${(currentTime - lastShakeTime)/1000}s), ignoring")
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationAndTriggerFallAlert() {
        try {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token)
                .addOnSuccessListener { location ->
                    if (location != null) {
                        lastLocation = location
                        Log.d(TAG, "Fall detected, updated location: ${location.latitude}, ${location.longitude}")
                    }
                    triggerFallAlert()
                }
                .addOnFailureListener {
                    // Proceed with alert even if location update fails
                    Log.e(TAG, "Failed to get current location, using last known location")
                    triggerFallAlert()
                }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating location", e)
            triggerFallAlert()
        }
    }

    private fun triggerFallAlert() {
        Log.i(TAG, "Triggering fall alert")
        
        // Send SMS with location to emergency contacts
        sendEmergencySMS()
        
        // Show notification to user
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("SHOW_FALL_CONFIRMATION", true)
        }
        Log.d(TAG, "Created intent with SHOW_FALL_CONFIRMATION=true")

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = createNotification(
            "Fall Detected!",
            "Tap to confirm if you're okay",
            pendingIntent
        )

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID + 1, notification)
        Log.i(TAG, "Fall notification displayed")
        
        // Removed Firebase logging
    }
    
    private fun sendEmergencySMS() {
        if (emergencyContacts.isEmpty()) {
            Log.w(TAG, "No emergency contacts available to send SMS")
            return
        }
        
        val locationStr = if (lastLocation != null) {
            "https://maps.google.com/?q=${lastLocation!!.latitude},${lastLocation!!.longitude}"
        } else {
            "Location unavailable"
        }
        
        val message = "EMERGENCY ALERT: A fall has been detected. Current location: $locationStr"
        
        try {
            val smsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                this.getSystemService(SmsManager::class.java)
            } else {
                @Suppress("DEPRECATION")
                SmsManager.getDefault()
            }
            
            for (contact in emergencyContacts) {
                smsManager.sendTextMessage(
                    contact,
                    null,
                    message,
                    null,
                    null
                )
                Log.i(TAG, "Emergency SMS sent to $contact")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send emergency SMS", e)
        }
    }
    
    // Removed logFallEvent method that used Firebase

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val name = "Fall Detection Service"
        val descriptionText = "Monitors for potential falls"
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
    ): android.app.Notification {
        val builder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(R.drawable.logochar)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        pendingIntent?.let {
            builder.setContentIntent(it)
            builder.setAutoCancel(true)
        }

        return builder.build()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Service onDestroy() called")
        if (wakeLock.isHeld) {
            wakeLock.release()
            Log.d(TAG, "WakeLock released")
        }
        sensorManager.unregisterListener(this)
        Log.d(TAG, "Sensor listener unregistered")
        isRunning = false
    }
}