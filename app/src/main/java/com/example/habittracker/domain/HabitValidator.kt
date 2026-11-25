package com.example.habittracker.domain

/** Валидация полей формы привычки. */
object HabitValidator {
    const val MAX_NAME_LENGTH = 40
    const val MAX_DESCRIPTION_LENGTH = 120

    enum class NameError { EMPTY, TOO_LONG }

    fun validateName(name: String): NameError? {
        val trimmed = name.trim()
        return when {
            trimmed.isEmpty() -> NameError.EMPTY
            trimmed.length > MAX_NAME_LENGTH -> NameError.TOO_LONG
            else -> null
        }
    }
}
