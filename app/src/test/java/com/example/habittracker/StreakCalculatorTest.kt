package com.example.habittracker

import com.example.habittracker.domain.StreakCalculator
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class StreakCalculatorTest {

    private val today = LocalDate.of(2026, 11, 25)

    private fun days(vararg offsets: Long): Set<LocalDate> = offsets.map { today.minusDays(it) }.toSet()

    @Test
    fun `no completions gives zero streak`() {
        assertEquals(0, StreakCalculator.currentStreak(emptySet(), today))
        assertEquals(0, StreakCalculator.bestStreak(emptySet()))
    }

    @Test
    fun `streak counts today and previous consecutive days`() {
        assertEquals(3, StreakCalculator.currentStreak(days(0, 1, 2), today))
    }

    @Test
    fun `streak stays alive when today is not done yet`() {
        assertEquals(2, StreakCalculator.currentStreak(days(1, 2), today))
    }

    @Test
    fun `streak resets after a missed day`() {
        // выполнено 2 и 3 дня назад, вчера и сегодня — нет
        assertEquals(0, StreakCalculator.currentStreak(days(2, 3), today))
    }

    @Test
    fun `gap splits the streak`() {
        assertEquals(2, StreakCalculator.currentStreak(days(0, 1, 3, 4, 5), today))
    }

    @Test
    fun `best streak finds the longest run`() {
        assertEquals(3, StreakCalculator.bestStreak(days(0, 1, 3, 4, 5, 9)))
    }

    @Test
    fun `best streak of a single day is one`() {
        assertEquals(1, StreakCalculator.bestStreak(days(7)))
    }

    @Test
    fun `streak works across month and year boundary`() {
        val newYear = LocalDate.of(2027, 1, 2)
        val done = setOf(LocalDate.of(2026, 12, 31), LocalDate.of(2027, 1, 1), newYear)
        assertEquals(3, StreakCalculator.currentStreak(done, newYear))
        assertEquals(3, StreakCalculator.bestStreak(done))
    }
}
