package com.example.fitnesstracker.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "performance_notes")
data class PerformanceNote(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: Long,
    val mood: Int,
    val notes: String
)
