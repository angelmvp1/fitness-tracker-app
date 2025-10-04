package com.example.fitnesstracker.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cycle_phases")
data class CyclePhase(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val startDate: Long,
    val phase: PhaseType,
    val compounds: List<CompoundDose>
)

enum class PhaseType { BLAST, CRUISE, OFF }

data class CompoundDose(
    val name: String,
    val dosageMg: Float,
    val frequencyPerWeek: Int
)
