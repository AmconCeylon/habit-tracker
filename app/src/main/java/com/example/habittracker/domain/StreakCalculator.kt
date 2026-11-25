package com.example.habittracker.domain

import java.time.LocalDate

/**
 * Чистая логика подсчёта серий (streak). Не зависит от Android — легко тестируется.
 */
object StreakCalculator {

    /**
     * Текущая серия: сколько дней подряд привычка выполнена до сегодняшнего дня включительно.
     * Если сегодня ещё не выполнено, серия не обрывается — считается от вчерашнего дня
     * (у пользователя есть время выполнить привычку до конца дня).
     */
    fun currentStreak(completed: Set<LocalDate>, today: LocalDate): Int {
        var day = if (today in completed) today else today.minusDays(1)
        var count = 0
        while (day in completed) {
            count++
            day = day.minusDays(1)
        }
        return count
    }

    /** Лучшая (самая длинная) серия за всё время. */
    fun bestStreak(completed: Collection<LocalDate>): Int {
        if (completed.isEmpty()) return 0
        val sorted = completed.toSortedSet().toList()
        var best = 1
        var current = 1
        for (i in 1 until sorted.size) {
            if (sorted[i - 1].plusDays(1) == sorted[i]) {
                current++
            } else {
                current = 1
            }
            if (current > best) best = current
        }
        return best
    }
}
