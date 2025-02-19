package com.example.myapplication

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.screens.*  // This will import all screens
import com.example.myapplication.viewmodel.MainViewModel
import com.example.myapplication.ui.theme.BetterAppTheme
import java.util.LinkedList
import java.util.Queue
import kotlin.math.abs
import kotlin.math.sqrt
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import com.example.myapplication.navigation.AppNavigation
import com.example.myapplication.ui.screens.LoginScreen
import com.example.myapplication.ui.screens.DashboardScreen

class MainActivity : ComponentActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var gyroscope: Sensor? = null
    private var magnetometer: Sensor? = null

    // data thresholds
    private companion object {
        const val FREE_FALL_THRESHOLD = 0.3f        // m/s² (near-zero gravity)
        const val IMPACT_THRESHOLD = 29.4f          // 3g acceleration
        const val POST_IMPACT_STATIONARY_THRESHOLD = 1.5f // m/s²
        const val ORIENTATION_CHANGE_THRESHOLD = 45f // degrees
        const val FREE_FALL_DURATION = 300L         // ms
        const val IMPACT_WINDOW = 500L              // ms after free-fall
        const val STATIONARY_DURATION = 2000L       // ms post-impact
        const val BUFFER_SIZE = 10
        const val ROTATION_THRESHOLD = 6.5f         // rad/s
    }

    private val accelerationBuffer: Queue<Float> = LinkedList()
    private enum class FallState { NORMAL, FREE_FALL_DETECTED, IMPACT_DETECTED }
    private var currentState = FallState.NORMAL
    private var freeFallStartTime = 0L
    private var impactTime = 0L
    private val handler = Handler(Looper.getMainLooper())
    private val gravity = FloatArray(3)
    private var geomagnetic = FloatArray(3)
    private val orientation = FloatArray(3)
    private var initialPitch = 0f
    private val rotationMatrix = FloatArray(9)

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeSensors()
        viewModel.startFallDetectionService(this)

        setContent {
            BetterAppTheme {
                AppNavigation(viewModel)
            }
        }
    }

    private fun initializeSensors() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST)
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_FASTEST)
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_FASTEST)
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> processAccelerometer(event)
            Sensor.TYPE_GYROSCOPE -> processGyroscope(event)
            Sensor.TYPE_MAGNETIC_FIELD -> geomagnetic = event.values.clone()
        }
    }

    private fun processAccelerometer(event: SensorEvent) {
        val alpha = 0.8f
        // Use temporary variables for gravity calculations
        val newGravityX = alpha * gravity[0] + (1 - alpha) * event.values[0]
        val newGravityY = alpha * gravity[1] + (1 - alpha) * event.values[1]
        val newGravityZ = alpha * gravity[2] + (1 - alpha) * event.values[2]

        // Update gravity array
        gravity[0] = newGravityX
        gravity[1] = newGravityY
        gravity[2] = newGravityZ

        val ax = event.values[0] - gravity[0]
        val ay = event.values[1] - gravity[1]
        val az = event.values[2] - gravity[2]
        val acceleration = sqrt((ax * ax + ay * ay + az * az).toDouble()).toFloat()

        updateAccelerationBuffer(acceleration)
        detectFreeFall(acceleration)
        checkImpact(acceleration)
        checkPostImpactCondition()
    }

    private fun processGyroscope(event: SensorEvent) {
        val rotationX = event.values[0]
        val rotationY = event.values[1]
        val rotationZ = event.values[2]

        val rotationMagnitude = sqrt(
            rotationX * rotationX +
                    rotationY * rotationY +
                    rotationZ * rotationZ
        )

        if (rotationMagnitude > ROTATION_THRESHOLD &&
            currentState == FallState.IMPACT_DETECTED) {
            confirmFall()
        }
    }

    private fun updateAccelerationBuffer(value: Float) {
        if (accelerationBuffer.size >= BUFFER_SIZE) accelerationBuffer.poll()
        accelerationBuffer.add(value)
    }

    private fun detectFreeFall(acceleration: Float) {
        when (currentState) {
            FallState.NORMAL -> {
                if (acceleration < FREE_FALL_THRESHOLD) {
                    currentState = FallState.FREE_FALL_DETECTED
                    freeFallStartTime = System.currentTimeMillis()
                    viewModel.updateFallAlertText("Free fall detected!")
                }
            }
            else -> {
                // Handle other states
            }
        }
    }

    private fun checkImpact(acceleration: Float) {
        if (currentState == FallState.FREE_FALL_DETECTED) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - freeFallStartTime > FREE_FALL_DURATION) {
                if (acceleration > IMPACT_THRESHOLD) {
                    currentState = FallState.IMPACT_DETECTED
                    impactTime = currentTime
                    viewModel.updateFallAlertText("Impact detected!")
                } else {
                    // Reset if no impact detected after free fall duration
                    currentState = FallState.NORMAL
                    viewModel.updateFallAlertText("")
                }
            }
        }
    }

    private fun checkPostImpactCondition() {
        if (currentState == FallState.IMPACT_DETECTED) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - impactTime > IMPACT_WINDOW) {
                // Check if person is stationary after impact
                val isStationary = accelerationBuffer.all {
                    abs(it) < POST_IMPACT_STATIONARY_THRESHOLD
                }
                if (isStationary) {
                    confirmFall()
                } else {
                    // Reset if movement detected
                    currentState = FallState.NORMAL
                    viewModel.updateFallAlertText("")
                }
            }
        }
    }

    private fun calculateAccelerationVariance(): Float {
        val mean = accelerationBuffer.average().toFloat()
        return accelerationBuffer.map { (it - mean) * (it - mean) }.average().toFloat()
    }

    private fun storeInitialOrientation() {
        if (SensorManager.getRotationMatrix(rotationMatrix, null, gravity, geomagnetic)) {
            SensorManager.getOrientation(rotationMatrix, orientation)
            initialPitch = abs(Math.toDegrees(orientation[1].toDouble())).toFloat()
        }
    }

    private fun calculateOrientationChange(): Boolean {
        if (SensorManager.getRotationMatrix(rotationMatrix, null, gravity, geomagnetic)) {
            SensorManager.getOrientation(rotationMatrix, orientation)
            val currentPitch = abs(Math.toDegrees(orientation[1].toDouble())).toFloat()
            return abs(currentPitch - initialPitch) > ORIENTATION_CHANGE_THRESHOLD
        }
        return false
    }

    private fun confirmFall() {
        currentState = FallState.NORMAL
        // Update fall alert text
        viewModel.updateFallAlertText("Fall detected! Are you okay?")
        // Reset buffers and timers
        accelerationBuffer.clear()
        freeFallStartTime = 0L
        impactTime = 0L
    }

    private fun cancelFreeFall() {
        if (currentState == FallState.FREE_FALL_DETECTED) {
            resetDetectionSystem()
        }
    }

    private fun resetDetectionSystem() {
        currentState = FallState.NORMAL
        freeFallStartTime = 0L
        impactTime = 0L
        accelerationBuffer.clear()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Handle accuracy changes if needed
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST)
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_FASTEST)
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_FASTEST)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.stopFallDetectionService(this)
    }
}