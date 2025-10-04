package com.example.fitnesstracker.data

import com.example.fitnesstracker.data.entities.BodyMetric
import com.example.fitnesstracker.data.entities.CyclePhase
import com.example.fitnesstracker.data.entities.PerformanceNote
import com.example.fitnesstracker.data.entities.Reminder
import com.example.fitnesstracker.data.entities.TrainingSession
import com.example.fitnesstracker.notifications.ReminderScheduler
import com.example.fitnesstracker.ui.screens.dashboard.VolumeCalculator
import com.example.fitnesstracker.DashboardState
import kotlinx.coroutines.flow.combine

class FitnessRepository(
    private val localDataSource: LocalDataSource,
    private val reminderScheduler: ReminderScheduler
) {

    fun observeDashboard(): Flow<DashboardState> {
        val sessionsFlow = localDataSource.observeTrainingSessions()
        val metricsFlow = localDataSource.observeBodyMetrics()
        val notesFlow = localDataSource.observeNotes()

        return combine(sessionsFlow, metricsFlow, notesFlow) { sessions, metrics, notes ->
            val latestMetric = metrics.firstOrNull()
            val lastSessions = sessions.take(7)
            val weeklyVolume = VolumeCalculator.calculateVolume(sessions)
            DashboardState(latestMetric, lastSessions, weeklyVolume, notes.take(14))
        }
    }

    fun observeCurrentCycle(): Flow<CyclePhase?> = localDataSource.observeCurrentCycle()

    fun observeReminders(): Flow<List<Reminder>> = localDataSource.observeReminders()

    suspend fun insertTrainingSession(session: TrainingSession) =
        localDataSource.insertTrainingSession(session)

    suspend fun insertBodyMetric(metric: BodyMetric) =
        localDataSource.insertBodyMetric(metric)

    suspend fun insertCyclePhase(phase: CyclePhase) =
        localDataSource.insertCyclePhase(phase)

    suspend fun insertPerformanceNote(note: PerformanceNote) =
        localDataSource.insertPerformanceNote(note)

    suspend fun upsertReminder(reminder: Reminder) {
        val id = localDataSource.upsertReminder(reminder)
        val scheduledReminder = if (reminder.id == 0L) reminder.copy(id = id) else reminder
        reminderScheduler.schedule(scheduledReminder)
    }

    suspend fun deleteReminder(reminder: Reminder) {
        localDataSource.deleteReminder(reminder)
        reminderScheduler.cancel(reminder)
    }
}
