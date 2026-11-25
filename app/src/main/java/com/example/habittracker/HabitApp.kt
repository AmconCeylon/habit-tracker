package com.example.habittracker

import android.app.Application
import com.example.habittracker.data.AppDatabase
import com.example.habittracker.data.HabitRepository
import com.example.habittracker.notifications.ReminderWorker
import com.example.habittracker.notifications.WorkManagerReminderScheduler

/** Простой ручной DI-контейнер (для проекта такого размера Hilt избыточен). */
class AppContainer(app: Application) {
    private val database = AppDatabase.create(app)
    val repository = HabitRepository(database.habitDao(), WorkManagerReminderScheduler(app))
}

class HabitApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        ReminderWorker.ensureChannel(this)
    }
}
