package com.example.fitnesstracker.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "body_metrics")
data class BodyMetric(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: Long,
    val weightKg: Float,
    val bodyFatPercent: Float,
    val chestCm: Float?,
    val waistCm: Float?,
    val armCm: Float?,
    val legCm: Float?
)
