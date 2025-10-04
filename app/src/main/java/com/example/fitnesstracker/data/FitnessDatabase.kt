package com.example.fitnesstracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.fitnesstracker.data.dao.FitnessDao
import com.example.fitnesstracker.data.entities.BodyMetric
import com.example.fitnesstracker.data.entities.Converters
import com.example.fitnesstracker.data.entities.CyclePhase
import com.example.fitnesstracker.data.entities.PerformanceNote
import com.example.fitnesstracker.data.entities.Reminder
import com.example.fitnesstracker.data.entities.TrainingSession

@Database(
    entities = [
        TrainingSession::class,
        BodyMetric::class,
        CyclePhase::class,
        PerformanceNote::class,
        Reminder::class
    ],
    version = 1
)
@TypeConverters(Converters::class)
abstract class FitnessDatabase : RoomDatabase() {
    abstract fun fitnessDao(): FitnessDao

    companion object {
        @Volatile
        private var instance: FitnessDatabase? = null

        fun get(context: Context): FitnessDatabase = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                FitnessDatabase::class.java,
                "fitness_tracker.db"
            ).build().also { instance = it }
        }
    }
}
