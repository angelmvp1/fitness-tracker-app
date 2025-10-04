package com.example.fitnesstracker.data.entities

data class ExerciseEntry(
    val name: String,
    val sets: List<ExerciseSet>
)

data class ExerciseSet(
    val repetitions: Int,
    val weight: Float
)
