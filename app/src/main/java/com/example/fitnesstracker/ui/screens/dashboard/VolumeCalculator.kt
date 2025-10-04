package com.example.fitnesstracker.ui.screens.dashboard

import com.example.fitnesstracker.data.entities.TrainingSession
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object VolumeCalculator {
    private val dayFormatter = SimpleDateFormat("EEE", Locale.getDefault())

    fun calculateVolume(sessions: List<TrainingSession>): Map<String, Float> {
        return sessions.groupBy { session ->
            dayFormatter.format(Date(session.date))
        }.mapValues { (_, daySessions) ->
            daySessions.sumOf { session ->
                session.exercises.sumOf { entry ->
                    entry.sets.sumOf { set ->
                        (set.weight * set.repetitions).toDouble()
                    }
                }
            }.toFloat()
        }
    }
}
