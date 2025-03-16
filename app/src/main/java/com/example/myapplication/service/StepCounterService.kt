package com.example.myapplication.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class StepCounterService : LifecycleService(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var stepCounterSensor: Sensor? = null
    private val CHANNEL_ID = "StepCounterChannel"
    private var stepCount = 0 // This should be updated with actual step count logic

    override fun onCreate() {
        super.onCreate()
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        createNotificationChannel()
        startForeground(1, getNotification())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Step Counter Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    private fun getNotification(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Step Counter")
            .setContentText("Steps: $stepCount")
            .setSmallIcon(R.drawable.better_logo)
            .setContentIntent(pendingIntent)
            .build()
    }

    // Call this method to update the step count and notification
    fun updateStepCount(newSteps: Int) {
        stepCount += newSteps
        val notification = getNotification()
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(1, notification)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            val steps = event.values[0].toInt()
            // Update the ViewModel or local storage with the new step count
            GlobalScope.launch {
                // Get the application context to access the database
                val appContext = applicationContext
                val stepDataDao = com.example.myapplication.data.AppDatabase.getDatabase(appContext).stepDataDao()
                
                // Calculate calories (simplified calculation)
                val calories = (steps * 0.04f).toInt()
                
                // Insert the step data into the database
                stepDataDao.insert(com.example.myapplication.data.StepDataEntity(
                    steps = steps,
                    calories = calories,
                    date = System.currentTimeMillis()
                ))
            }
            updateStepCount(steps)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
    }


}