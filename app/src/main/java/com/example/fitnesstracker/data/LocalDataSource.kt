package com.example.fitnesstracker.data

import android.content.Context
import com.example.fitnesstracker.data.dao.FitnessDao
import com.example.fitnesstracker.data.entities.BodyMetric
import com.example.fitnesstracker.data.entities.CyclePhase
import com.example.fitnesstracker.data.entities.PerformanceNote
import com.example.fitnesstracker.data.entities.Reminder
import com.example.fitnesstracker.data.entities.TrainingSession
import kotlinx.coroutines.flow.Flow

class LocalDataSource(context: Context) {
    private val dao: FitnessDao = FitnessDatabase.get(context).fitnessDao()

    fun observeTrainingSessions(): Flow<List<TrainingSession>> = dao.observeTrainingSessions()
    suspend fun insertTrainingSession(session: TrainingSession) = dao.insertTrainingSession(session)

    fun observeBodyMetrics(): Flow<List<BodyMetric>> = dao.observeBodyMetrics()
    suspend fun insertBodyMetric(metric: BodyMetric) = dao.insertBodyMetric(metric)

    fun observeCurrentCycle(): Flow<CyclePhase?> = dao.observeCurrentCycle()
    suspend fun insertCyclePhase(phase: CyclePhase) = dao.insertCyclePhase(phase)

    fun observeNotes(): Flow<List<PerformanceNote>> = dao.observePerformanceNotes()
    suspend fun insertPerformanceNote(note: PerformanceNote) = dao.insertPerformanceNote(note)

    fun observeReminders(): Flow<List<Reminder>> = dao.observeReminders()
    suspend fun upsertReminder(reminder: Reminder): Long = dao.upsertReminder(reminder)
    suspend fun deleteReminder(reminder: Reminder) = dao.deleteReminder(reminder)
}
