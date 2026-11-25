package com.example.habittracker.domain

import java.time.LocalDate

/** Статистика по одной привычке. */
data class HabitStats(
    val currentStreak: Int,
    val bestStreak: Int,
    val totalCompletions: Int,
    /** Доля выполненных дней за последние [WINDOW_DAYS] дней, 0..100. */
    val completionRatePercent: Int,
    val doneToday: Boolean,
) {
    companion object {
        const val WINDOW_DAYS = 30

        fun calculate(completed: Set<LocalDate>, today: LocalDate, createdOn: LocalDate): HabitStats {
            // Окно не может начинаться раньше даты создания привычки, иначе процент занижается.
            val windowStart = maxOf(today.minusDays((WINDOW_DAYS - 1).toLong()), createdOn)
            val daysInWindow = (today.toEpochDay() - windowStart.toEpochDay() + 1).toInt().coerceAtLeast(1)
            val doneInWindow = completed.count { it >= windowStart && it <= today }
            val rate = (doneInWindow * 100.0 / daysInWindow).toInt().coerceIn(0, 100)
            return HabitStats(
                currentStreak = StreakCalculator.currentStreak(completed, today),
                bestStreak = StreakCalculator.bestStreak(completed),
                totalCompletions = completed.size,
                completionRatePercent = rate,
                doneToday = today in completed,
            )
        }
    }
}
