package com.example.myapplication.viewmodel


import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.myapplication.data.StepData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import java.util.Calendar
import android.util.Log
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.delay
import kotlinx.coroutines.Job
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.data.AppDatabase
import com.example.myapplication.data.StepDataEntity
import com.example.myapplication.data.WeeklySummary


private val Context.dataStore by preferencesDataStore(name = "step_counter")

class StepCounterViewModel(application: Application) : AndroidViewModel(application), SensorEventListener {
    private val sensorManager = application.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    private val stepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

    private val _stepData = MutableStateFlow(StepData())
    val stepData = _stepData.asStateFlow()

    private var initialSteps = 0
    private var lastDaySteps = 0

    private var timerJob: Job? = null

    private val stepDataDao = AppDatabase.getDatabase(application).stepDataDao()

    companion object {
        private val STEPS_KEY = intPreferencesKey("steps_key")
        private val WEIGHT_KEY = floatPreferencesKey("user_weight")
    }

    init {
        viewModelScope.launch {
            Log.d("StepCounter", "Initializing StepCounterViewModel")
            try {
                lastDaySteps = getApplication<Application>().dataStore.data.first()[STEPS_KEY] ?: 0
                Log.d("StepCounter", "Loaded last day steps: $lastDaySteps")
            } catch (e: Exception) {
                Log.e("StepCounter", "Error loading steps", e)
                lastDaySteps = 0
            }

            // Check if we have permission
            val hasPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                getApplication<Application>().checkSelfPermission(
                    Manifest.permission.ACTIVITY_RECOGNITION
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
            Log.d("StepCounter", "Has ACTIVITY_RECOGNITION permission: $hasPermission")

            // Log available sensors
            Log.d("StepCounter", "Step Counter available: ${stepCounter != null}")
            Log.d("StepCounter", "Step Detector available: ${stepDetector != null}")

            // List all sensors
            val sensors = sensorManager.getSensorList(Sensor.TYPE_ALL)
            Log.d("StepCounter", "Available sensors (${sensors.size}):")
            sensors.forEach { sensor ->
                Log.d("StepCounter", "- ${sensor.name} (${sensor.stringType})")
            }

            if (hasPermission) {
                registerSensors()
            } else {
                Log.e("StepCounter", "Cannot register sensors - missing permission")
                _stepData.value = _stepData.value.copy(steps = -1)
            }
        }

        viewModelScope.launch {
            // Load user weight from data store
            val userWeight = getApplication<Application>().dataStore.data.first()[WEIGHT_KEY] ?: 70f // Default weight
            _stepData.value = _stepData.value.copy(userWeight = userWeight)
            Log.d("StepCounter", "Loaded user weight: $userWeight")
        }
    }

    private fun registerSensors() {
        var registered = false

        stepCounter?.let { sensor ->
            val success = sensorManager.registerListener(
                this,
                sensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
            Log.d("StepCounter", "Step Counter registration ${if (success) "successful" else "failed"}")
            registered = registered || success
        } ?: Log.e("StepCounter", "Step Counter sensor not available")

        stepDetector?.let { sensor ->
            val success = sensorManager.registerListener(
                this,
                sensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
            Log.d("StepCounter", "Step Detector registration ${if (success) "successful" else "failed"}")
            registered = registered || success
        } ?: Log.e("StepCounter", "Step Detector sensor not available")

        if (!registered) {
            Log.e("StepCounter", "No step sensors could be registered")
            _stepData.value = _stepData.value.copy(steps = -1)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val sensorType = when(event.sensor.type) {
                Sensor.TYPE_STEP_COUNTER -> "Step Counter"
                Sensor.TYPE_STEP_DETECTOR -> "Step Detector"
                else -> "Unknown"
            }
            Log.d("StepCounter", "$sensorType event - values: ${event.values.joinToString()}")

            when (event.sensor.type) {
                Sensor.TYPE_STEP_COUNTER -> handleStepCounterEvent(event)
                Sensor.TYPE_STEP_DETECTOR -> handleStepDetectorEvent(event)
            }
        }
    }

    private fun handleStepCounterEvent(event: SensorEvent) {
        if (initialSteps == 0) {
            initialSteps = event.values[0].toInt()
            Log.d("StepCounter", "Initial steps set to: $initialSteps")
        }

        val currentSteps = event.values[0].toInt() - initialSteps + lastDaySteps
        Log.d("StepCounter", "Step Counter - Raw value: ${event.values[0]}, Current steps: $currentSteps")
        updateStepData(currentSteps)
    }

    private fun handleStepDetectorEvent(event: SensorEvent) {
        // Step detector sends a value of 1.0 for each step
        val steps = _stepData.value.steps + 1
        Log.d("StepCounter", "Step Detector - New step detected, total steps: $steps")
        updateStepData(steps)
    }

    private fun updateStepData(steps: Int) {
        val calories = calculateCalories(steps)
        val distance = calculateDistance(steps)

        Log.d("StepCounter", "Updating UI - Steps: $steps, Calories: $calories, Distance: $distance")

        _stepData.value = _stepData.value.copy(
            steps = steps,
            calories = calories,
            distance = distance
        )
    }

    private fun calculateCalories(steps: Int): Int {
        // Average calories burned per step (approximation)
        val caloriesPerStep = 0.04f * _stepData.value.userWeight
        return (steps * caloriesPerStep).toInt()
    }

    private fun calculateDistance(steps: Int): Float {
        // Average stride length in meters (approximation)
        val strideLength = 0.762f
        return (steps * strideLength) / 1000 // Convert to kilometers
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while(true) {
                delay(1000)
                _stepData.value = _stepData.value.copy(
                    timerSeconds = _stepData.value.timerSeconds + 1
                )
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
        _stepData.value = _stepData.value.copy(isTimerRunning = false)
    }

    fun toggleTimer() {
        if (_stepData.value.isTimerRunning) {
            stopTimer()
        } else {
            startTimer()
            _stepData.value = _stepData.value.copy(isTimerRunning = true)
        }
    }

    fun resetAll() {
        stopTimer()
        initialSteps = 0
        lastDaySteps = 0
        _stepData.value = StepData()
        viewModelScope.launch {
            getApplication<Application>().dataStore.edit { preferences ->
                preferences[STEPS_KEY] = 0
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
        sensorManager.unregisterListener(this)
        viewModelScope.launch {
            getApplication<Application>().dataStore.edit { preferences ->
                preferences[STEPS_KEY] = _stepData.value.steps
            }
        }

    }

    fun updateMoveGoal(newGoal: Int) {
        viewModelScope.launch {
            _stepData.value = _stepData.value.copy(goal = newGoal)
            Log.d("StepCounter", "Updated move goal to: $newGoal")
        }
    }

    fun insertStepData(steps: Int, calories: Int) {
        viewModelScope.launch {
            val stepData = StepDataEntity(steps = steps, calories = calories, date = System.currentTimeMillis())
            stepDataDao.insert(stepData)
        }
    }

    fun getWeeklySummary(startDate: Long, endDate: Long): LiveData<WeeklySummary> {
        val summary = MutableLiveData<WeeklySummary>()
        viewModelScope.launch {
            summary.postValue(stepDataDao.getWeeklySummary(startDate, endDate))
        }
        return summary
    }
}