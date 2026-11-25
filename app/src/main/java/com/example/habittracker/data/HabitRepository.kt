package com.example.habittracker.data

import com.example.habittracker.domain.Habit
import com.example.habittracker.domain.HabitStats
import com.example.habittracker.domain.HabitWithStats
import com.example.habittracker.notifications.ReminderScheduler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate

/**
 * Единая точка доступа к данным. ViewModel'и работают только с репозиторием.
 * Планирование напоминаний тоже здесь, чтобы БД и уведомления не расходились.
 */
class HabitRepository(
    private val dao: HabitDao,
    private val reminders: ReminderScheduler,
) {

    /** Все привычки со статистикой на сегодня. */
    fun observeHabitsWithStats(): Flow<List<HabitWithStats>> =
        combine(dao.observeHabits(), dao.observeAllCompletions()) { habits, completions ->
            val today = LocalDate.now()
            val byHabit = completions.groupBy({ it.habitId }, { LocalDate.ofEpochDay(it.epochDay) })
            habits.map { entity ->
                val dates = byHabit[entity.id].orEmpty().toSet()
                val habit = entity.toDomain()
                HabitWithStats(habit, HabitStats.calculate(dates, today, habit.createdOn), dates)
            }
        }

    fun observeHabitWithStats(id: Long): Flow<HabitWithStats?> =
        combine(dao.observeHabit(id), dao.observeCompletions(id)) { entity, completions ->
            entity?.let {
                val dates = completions.map { c -> LocalDate.ofEpochDay(c.epochDay) }.toSet()
                val habit = it.toDomain()
                HabitWithStats(habit, HabitStats.calculate(dates, LocalDate.now(), habit.createdOn), dates)
            }
        }

    suspend fun getHabit(id: Long): Habit? = dao.getHabit(id)?.toDomain()

    /** Создаёт или обновляет привычку (id == 0 — создание). */
    suspend fun saveHabit(habit: Habit) {
        val cleaned = habit.copy(name = habit.name.trim(), description = habit.description.trim())
        val id = if (cleaned.id == 0L) {
            dao.insertHabit(cleaned.toEntity())
        } else {
            dao.updateHabit(cleaned.toEntity())
            cleaned.id
        }
        if (cleaned.reminderEnabled) {
            reminders.schedule(id, cleaned.reminderHour, cleaned.reminderMinute)
        } else {
            reminders.cancel(id)
        }
    }

    suspend fun deleteHabit(habit: Habit) {
        reminders.cancel(habit.id)
        dao.deleteHabit(habit.toEntity())
    }

    /** Отметить / снять отметку выполнения за [date]. */
    suspend fun setCompleted(habitId: Long, date: LocalDate, completed: Boolean) {
        if (completed) {
            dao.insertCompletion(CompletionEntity(habitId, date.toEpochDay()))
        } else {
            dao.deleteCompletion(habitId, date.toEpochDay())
        }
    }

    suspend fun isCompleted(habitId: Long, date: LocalDate): Boolean =
        dao.isCompleted(habitId, date.toEpochDay())
}
