package com.example.habittracker.domain

import java.time.LocalDate

/** Доменная модель привычки (не зависит от Room). */
data class Habit(
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val reminderEnabled: Boolean = false,
    val reminderHour: Int = 9,
    val reminderMinute: Int = 0,
    val createdOn: LocalDate = LocalDate.now(),
)

/** Привычка + её статистика + выполненные даты (для списка и экрана деталей). */
data class HabitWithStats(
    val habit: Habit,
    val stats: HabitStats,
    val completedDates: Set<LocalDate>,
)
