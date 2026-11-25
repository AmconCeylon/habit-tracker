package com.example.habittracker.notifications

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.habittracker.HabitApp
import com.example.habittracker.MainActivity
import com.example.habittracker.R
import java.time.LocalDate

class ReminderWorker(
    private val appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val habitId = inputData.getLong(WorkManagerReminderScheduler.KEY_HABIT_ID, -1L)
        val hour = inputData.getInt(WorkManagerReminderScheduler.KEY_HOUR, 9)
        val minute = inputData.getInt(WorkManagerReminderScheduler.KEY_MINUTE, 0)
        if (habitId < 0) return Result.failure()

        val repository = (appContext.applicationContext as HabitApp).container.repository
        val habit = repository.getHabit(habitId)

        // Привычку могли удалить или отключить напоминание — тогда цепочку не продолжаем.
        if (habit == null || !habit.reminderEnabled) return Result.success()

        // Не беспокоим, если сегодня уже выполнено.
        if (!repository.isCompleted(habitId, LocalDate.now())) {
            showNotification(habitId, habit.name)
        }

        // Планируем следующий день.
        WorkManagerReminderScheduler.enqueue(appContext, habitId, hour, minute)
        return Result.success()
    }

    @SuppressLint("MissingPermission") // разрешение проверяется ниже
    private fun showNotification(habitId: Long, habitName: String) {
        val hasPermission = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(appContext, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
        if (!hasPermission) return

        ensureChannel(appContext)
        val openApp = PendingIntent.getActivity(
            appContext,
            habitId.toInt(),
            Intent(appContext, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        val notification = NotificationCompat.Builder(appContext, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(appContext.getString(R.string.notification_title))
            .setContentText(habitName)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(openApp)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(appContext).notify(habitId.toInt(), notification)
    }

    companion object {
        const val CHANNEL_ID = "habit_reminders"

        fun ensureChannel(context: Context) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
            val manager = context.getSystemService(NotificationManager::class.java)
            if (manager.getNotificationChannel(CHANNEL_ID) == null) {
                manager.createNotificationChannel(
                    NotificationChannel(
                        CHANNEL_ID,
                        context.getString(R.string.channel_name),
                        NotificationManager.IMPORTANCE_DEFAULT,
                    ).apply { description = context.getString(R.string.channel_description) },
                )
            }
        }
    }
}
