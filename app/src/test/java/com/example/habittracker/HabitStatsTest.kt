package com.example.habittracker

import com.example.habittracker.domain.HabitStats
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class HabitStatsTest {

    private val today = LocalDate.of(2026, 11, 25)

    @Test
    fun `new habit created today has no division by zero`() {
        val stats = HabitStats.calculate(emptySet(), today, createdOn = today)
        assertEquals(0, stats.completionRatePercent)
        assertFalse(stats.doneToday)
    }

    @Test
    fun `rate is counted from creation date for young habits`() {
        // привычка создана 4 дня назад (окно = 5 дней), выполнена в 2 из них
        val created = today.minusDays(4)
        val stats = HabitStats.calculate(setOf(today, today.minusDays(1)), today, created)
        assertEquals(40, stats.completionRatePercent)
        assertTrue(stats.doneToday)
    }

    @Test
    fun `rate uses 30 day window for old habits`() {
        val created = today.minusDays(100)
        val done = (0L until 15L).map { today.minusDays(it) }.toSet()
        val stats = HabitStats.calculate(done, today, created)
        assertEquals(50, stats.completionRatePercent)
        assertEquals(15, stats.totalCompletions)
        assertEquals(15, stats.currentStreak)
    }

    @Test
    fun `completions outside the window are ignored in rate but counted in total`() {
        val created = today.minusDays(100)
        val done = setOf(today.minusDays(60), today.minusDays(61))
        val stats = HabitStats.calculate(done, today, created)
        assertEquals(0, stats.completionRatePercent)
        assertEquals(2, stats.totalCompletions)
        assertEquals(2, stats.bestStreak)
    }
}
