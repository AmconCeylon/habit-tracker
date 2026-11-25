package com.example.habittracker

import com.example.habittracker.notifications.ReminderTime
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Duration
import java.time.LocalDateTime

class ReminderTimeTest {

    @Test
    fun `reminder later today`() {
        val now = LocalDateTime.of(2026, 11, 25, 8, 0)
        assertEquals(Duration.ofHours(1).plusMinutes(30), ReminderTime.delayUntilNext(now, 9, 30))
    }

    @Test
    fun `reminder time already passed goes to tomorrow`() {
        val now = LocalDateTime.of(2026, 11, 25, 10, 0)
        assertEquals(Duration.ofHours(23), ReminderTime.delayUntilNext(now, 9, 0))
    }

    @Test
    fun `reminder exactly now goes to tomorrow`() {
        val now = LocalDateTime.of(2026, 11, 25, 9, 0)
        assertEquals(Duration.ofHours(24), ReminderTime.delayUntilNext(now, 9, 0))
    }

    @Test
    fun `reminder across midnight`() {
        val now = LocalDateTime.of(2026, 12, 31, 23, 30)
        assertEquals(Duration.ofHours(1), ReminderTime.delayUntilNext(now, 0, 30))
    }
}
