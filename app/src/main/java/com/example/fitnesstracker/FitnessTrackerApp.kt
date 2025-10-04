package com.example.fitnesstracker

import android.app.Application
import com.example.fitnesstracker.notifications.ReminderScheduler

class FitnessTrackerApp : Application() {
    lateinit var reminderScheduler: ReminderScheduler
        private set

    override fun onCreate() {
        super.onCreate()
        reminderScheduler = ReminderScheduler(this)
    }
}
