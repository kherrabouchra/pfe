package com.example.myapplication.service

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.PowerManager
import java.util.*
import kotlin.math.sqrt

/**
 * Class that implements the sleep detection algorithm based on sensor data
 * as described in sleep.md
 */
class SleepDetector(private val context: Context) : SensorEventListener {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val gyroscope: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
    
    private var lastMovementTime = System.currentTimeMillis()
    private var lastRotationTime = System.currentTimeMillis()
    private var isRunning = false
    
    private var accelerometerMagnitude = 0f
    private var rotationMagnitude = 0f
    
    private var callback: ((Boolean, Float, Float, Boolean) -> Unit)? = null
    
    companion object {
        private const val TAG = "SleepDetector"
        
        // Constants from sleep.md
        const val SCREEN_OFF_THRESHOLD = 30 * 60 * 1000L // 30 minutes in milliseconds
        const val MOVEMENT_THRESHOLD = 1.5f // m/s²
        const val ROTATION_THRESHOLD = 0.5f // rad/s
        const val INACTIVITY_DURATION_THRESHOLD = 30 * 60 * 1000L // 30 minutes in milliseconds
    }
    
    /**
     * Start sleep detection with a callback for updates
     */
    fun startDetection(onUpdate: (Boolean, Float, Float, Boolean) -> Unit) {
        if (isRunning) return
        
        callback = onUpdate
        
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        
        gyroscope?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        
        isRunning = true
        
        // Start periodic updates
        Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                if (isRunning) {
                    val isUserSleeping = isUserSleeping()
                    val isScreenOff = isScreenOff()
                    
                    callback?.invoke(isUserSleeping, accelerometerMagnitude, rotationMagnitude, isScreenOff)
                }
            }
        }, 0, 5000) // Update every 5 seconds
    }
    
    /**
     * Stop sleep detection
     */
    fun stopDetection() {
        if (!isRunning) return
        
        sensorManager.unregisterListener(this)
        isRunning = false
        callback = null
    }
    
    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                
                accelerometerMagnitude = calculateMovement(x, y, z)
                
                if (accelerometerMagnitude > MOVEMENT_THRESHOLD) {
                    lastMovementTime = System.currentTimeMillis()
                }
            }
            
            Sensor.TYPE_GYROSCOPE -> {
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                
                rotationMagnitude = calculateRotation(x, y, z)
                
                if (rotationMagnitude > ROTATION_THRESHOLD) {
                    lastRotationTime = System.currentTimeMillis()
                }
            }
        }
    }
    
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used
    }
    
    /**
     * Check if it is night time (between 10 PM and 6 AM)
     */
    private fun isNightTime(): Boolean {
        val calendar = Calendar.getInstance()
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
        
        return hourOfDay in 22..23 || hourOfDay in 0..5
    }
    
    /**
     * Check if screen is off
     */
    private fun isScreenOff(): Boolean {
        return !powerManager.isInteractive
    }
    
    /**
     * Calculate movement magnitude from accelerometer data
     */
    private fun calculateMovement(x: Float, y: Float, z: Float): Float {
        return sqrt(x * x + y * y + z * z)
    }
    
    /**
     * Calculate rotation magnitude from gyroscope data
     */
    private fun calculateRotation(x: Float, y: Float, z: Float): Float {
        return sqrt(x * x + y * y + z * z)
    }
    
    /**
     * Calculate duration of inactivity based on last movement time
     */
    private fun calculateInactivityDuration(): Long {
        return System.currentTimeMillis() - lastMovementTime
    }
    
    /**
     * Calculate duration since last rotation
     */
    private fun calculateRotationDuration(): Long {
        return System.currentTimeMillis() - lastRotationTime
    }
    
    /**
     * Determine if user is sleeping based on all sensor data
     */
    private fun isUserSleeping(): Boolean {
        // Step 1: Check if it is night time
        if (!isNightTime()) {
            return false  // Not sleeping if it's not nighttime
        }
        
        // Step 2: Check if the screen has been off for a prolonged period
        if (!isScreenOff()) {
            return false  // User might be awake if screen is on
        }
        
        // Step 3: Check if there has been no significant movement for 30 minutes
        val inactivityDuration = calculateInactivityDuration()
        if (inactivityDuration < INACTIVITY_DURATION_THRESHOLD) {
            return false  // User is still active (moving)
        }
        
        // Step 4: Check if there has been no significant rotation for 30 minutes
        val rotationDuration = calculateRotationDuration()
        if (rotationDuration < INACTIVITY_DURATION_THRESHOLD) {
            return false  // User is not rotating, but may be active in other ways
        }
        
        // If all conditions are met, user is likely asleep
        return true
    }
}