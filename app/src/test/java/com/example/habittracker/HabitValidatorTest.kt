package com.example.habittracker

import com.example.habittracker.domain.HabitValidator
import com.example.habittracker.domain.HabitValidator.NameError
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class HabitValidatorTest {

    @Test
    fun `empty and blank names are rejected`() {
        assertEquals(NameError.EMPTY, HabitValidator.validateName(""))
        assertEquals(NameError.EMPTY, HabitValidator.validateName("   "))
    }

    @Test
    fun `too long name is rejected`() {
        assertEquals(NameError.TOO_LONG, HabitValidator.validateName("a".repeat(HabitValidator.MAX_NAME_LENGTH + 1)))
    }

    @Test
    fun `valid names are accepted`() {
        assertNull(HabitValidator.validateName("Пить воду"))
        assertNull(HabitValidator.validateName("a".repeat(HabitValidator.MAX_NAME_LENGTH)))
        assertNull(HabitValidator.validateName("  Зарядка  "))
    }
}
