package com.example.fitnesstracker.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fitnesstracker.data.entities.BodyMetric
import com.example.fitnesstracker.data.entities.CyclePhase
import com.example.fitnesstracker.data.entities.PerformanceNote
import com.example.fitnesstracker.data.entities.Reminder
import com.example.fitnesstracker.data.entities.TrainingSession
import kotlinx.coroutines.flow.Flow

@Dao
interface FitnessDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrainingSession(session: TrainingSession)

    @Query("SELECT * FROM training_sessions ORDER BY date DESC")
    fun observeTrainingSessions(): Flow<List<TrainingSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBodyMetric(metric: BodyMetric)

    @Query("SELECT * FROM body_metrics ORDER BY date DESC")
    fun observeBodyMetrics(): Flow<List<BodyMetric>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCyclePhase(phase: CyclePhase)

    @Query("SELECT * FROM cycle_phases ORDER BY startDate DESC LIMIT 1")
    fun observeCurrentCycle(): Flow<CyclePhase?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPerformanceNote(note: PerformanceNote)

    @Query("SELECT * FROM performance_notes ORDER BY date DESC")
    fun observePerformanceNotes(): Flow<List<PerformanceNote>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertReminder(reminder: Reminder): Long

    @Delete
    suspend fun deleteReminder(reminder: Reminder)

    @Query("SELECT * FROM reminders ORDER BY triggerAt ASC")
    fun observeReminders(): Flow<List<Reminder>>
}
