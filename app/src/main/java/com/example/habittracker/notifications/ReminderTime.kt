package com.example.habittracker.notifications

import java.time.Duration
import java.time.LocalDateTime

/** Чистая логика расчёта времени следующего напоминания. */
object ReminderTime {

    /** Задержка до ближайшего срабатывания в [hour]:[minute], строго в будущем. */
    fun delayUntilNext(now: LocalDateTime, hour: Int, minute: Int): Duration {
        var next = now.toLocalDate().atTime(hour, minute)
        if (!next.isAfter(now)) next = next.plusDays(1)
        return Duration.between(now, next)
    }
}
