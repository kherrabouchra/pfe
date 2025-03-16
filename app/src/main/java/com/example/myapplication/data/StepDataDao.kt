package com.example.myapplication.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.myapplication.data.WeeklySummary

@Dao
interface StepDataDao {
    @Insert
    suspend fun insert(stepData: StepDataEntity)

    @Query("SELECT SUM(steps) as totalSteps, SUM(calories) as totalCalories FROM step_data WHERE date >= :startDate AND date <= :endDate")
    suspend fun getWeeklySummary(startDate: Long, endDate: Long): WeeklySummary
}