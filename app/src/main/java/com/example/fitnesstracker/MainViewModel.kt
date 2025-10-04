package com.example.fitnesstracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.fitnesstracker.data.FitnessRepository
import com.example.fitnesstracker.data.LocalDataSource
import com.example.fitnesstracker.data.entities.BodyMetric
import com.example.fitnesstracker.data.entities.CyclePhase
import com.example.fitnesstracker.data.entities.PerformanceNote
import com.example.fitnesstracker.data.entities.Reminder
import com.example.fitnesstracker.data.entities.TrainingSession
import com.example.fitnesstracker.notifications.ReminderScheduler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(private val repository: FitnessRepository) : ViewModel() {

    val dashboardState: StateFlow<DashboardState> = repository.observeDashboard()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DashboardState())

    val currentCycle: StateFlow<CyclePhase?> = repository.observeCurrentCycle()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val reminders: StateFlow<List<Reminder>> = repository.observeReminders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addTrainingSession(session: TrainingSession) {
        viewModelScope.launch { repository.insertTrainingSession(session) }
    }

    fun addBodyMetric(metric: BodyMetric) {
        viewModelScope.launch { repository.insertBodyMetric(metric) }
    }

    fun updateCycle(phase: CyclePhase) {
        viewModelScope.launch { repository.insertCyclePhase(phase) }
    }

    fun addPerformanceNote(note: PerformanceNote) {
        viewModelScope.launch { repository.insertPerformanceNote(note) }
    }

    fun scheduleReminder(reminder: Reminder) {
        viewModelScope.launch { repository.upsertReminder(reminder) }
    }

    fun deleteReminder(reminder: Reminder) {
        viewModelScope.launch { repository.deleteReminder(reminder) }
    }

    class Factory(private val scheduler: ReminderScheduler) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val dataSource = LocalDataSource(scheduler.context)
            val repository = FitnessRepository(dataSource, scheduler)
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
    }
}

data class DashboardState(
    val latestMetrics: BodyMetric? = null,
    val lastSessions: List<TrainingSession> = emptyList(),
    val weeklyVolume: Map<String, Float> = emptyMap(),
    val notes: List<PerformanceNote> = emptyList()
)
