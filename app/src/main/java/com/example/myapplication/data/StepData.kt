package com.example.myapplication.data
data class StepData(
    val steps: Int = 0,
    val goal: Int = 10000,
    val calories: Int = 0,
    val caloriesGoal: Int = 500,
    val distance: Float = 0f,
    val distanceGoal: Float = 5f, // 5 kilometers
    val timerSeconds: Long = 0,
    val timerGoal: Long = 3600, // 1 hour in seconds
    val isTimerRunning: Boolean = false,
    val userWeight: Float = 70f // Default weight in kg
) {
    val stepsProgressPercentage: Float get() = (steps.toFloat() / goal).coerceIn(0f, 1f)
    val caloriesProgressPercentage: Float get() = (calories.toFloat() / caloriesGoal).coerceIn(0f, 1f)
    val distanceProgressPercentage: Float get() = (distance / distanceGoal).coerceIn(0f, 1f)
    val timerProgressPercentage: Float get() = (timerSeconds.toFloat() / timerGoal).coerceIn(0f, 1f)
    
    fun formatTimer(): String {
        val hours = timerSeconds / 3600
        val minutes = (timerSeconds % 3600) / 60
        val seconds = timerSeconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
} 