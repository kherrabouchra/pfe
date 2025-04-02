package com.example.myapplication.data.model

import java.util.*

data class UserProfile(
    val userId: String = "",
    val name: String = "",
    val age: Int = 0,
    val gender: String = "",
    val weight: Float = 0f,
    val height: Float = 0f,
    val healthConditions: List<String> = listOf(),
    val mobilityLevel: String = "Normal", // Normal, Limited, Assisted
    val dailyWaterGoal: Int = 2000, // in ml
    val notificationPreferences: NotificationPreferences = NotificationPreferences()
)

data class NotificationPreferences(
    val enableReminders: Boolean = true,
    val reminderFrequency: String = "Medium", // Low, Medium, High
    val quietHoursStart: Int = 22, // 10 PM
    val quietHoursEnd: Int = 7 // 7 AM
)