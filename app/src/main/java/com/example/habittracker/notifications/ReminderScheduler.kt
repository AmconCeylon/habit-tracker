package com.example.habittracker.notifications

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

interface ReminderScheduler {
    fun schedule(habitId: Long, hour: Int, minute: Int)
    fun cancel(habitId: Long)
}

/**
 * Ежедневное напоминание через WorkManager: одноразовая работа, которая после срабатывания
 * сама планирует следующий день. WorkManager переживает перезагрузку устройства.
 */
class WorkManagerReminderScheduler(private val context: Context) : ReminderScheduler {

    override fun schedule(habitId: Long, hour: Int, minute: Int) {
        enqueue(context, habitId, hour, minute)
    }

    override fun cancel(habitId: Long) {
        WorkManager.getInstance(context).cancelUniqueWork(uniqueName(habitId))
    }

    companion object {
        const val KEY_HABIT_ID = "habit_id"
        const val KEY_HOUR = "hour"
        const val KEY_MINUTE = "minute"

        fun uniqueName(habitId: Long) = "reminder_$habitId"

        fun enqueue(context: Context, habitId: Long, hour: Int, minute: Int) {
            val delay = ReminderTime.delayUntilNext(LocalDateTime.now(), hour, minute)
            val request = OneTimeWorkRequestBuilder<ReminderWorker>()
                .setInitialDelay(delay.toMillis(), TimeUnit.MILLISECONDS)
                .setInputData(
                    Data.Builder()
                        .putLong(KEY_HABIT_ID, habitId)
                        .putInt(KEY_HOUR, hour)
                        .putInt(KEY_MINUTE, minute)
                        .build(),
                )
                .build()
            WorkManager.getInstance(context)
                .enqueueUniqueWork(uniqueName(habitId), ExistingWorkPolicy.REPLACE, request)
        }
    }
}
