package com.example.myapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "step_data")
data class StepDataEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val steps: Int,
    val calories: Int,
    val date: Long // Store the date as a timestamp
) 