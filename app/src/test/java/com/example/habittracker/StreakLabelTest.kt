package com.example.habittracker

import com.example.habittracker.ui.streakLabel
import org.junit.Assert.assertEquals
import org.junit.Test

class StreakLabelTest {
    @Test
    fun `russian plural forms`() {
        assertEquals("0 дней подряд", streakLabel(0))
        assertEquals("1 день подряд", streakLabel(1))
        assertEquals("2 дня подряд", streakLabel(2))
        assertEquals("5 дней подряд", streakLabel(5))
        assertEquals("11 дней подряд", streakLabel(11))
        assertEquals("21 день подряд", streakLabel(21))
        assertEquals("24 дня подряд", streakLabel(24))
        assertEquals("112 дней подряд", streakLabel(112))
    }
}
