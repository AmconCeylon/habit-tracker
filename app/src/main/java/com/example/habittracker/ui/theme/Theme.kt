package com.example.habittracker.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF2E7D32),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFB9F6CA),
    onPrimaryContainer = Color(0xFF00210B),
    secondary = Color(0xFF52634F),
    background = Color(0xFFF7FBF3),
    surface = Color(0xFFF7FBF3),
    surfaceVariant = Color(0xFFDDE5D9),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF7ADC8C),
    onPrimary = Color(0xFF00390F),
    primaryContainer = Color(0xFF1B5E20),
    onPrimaryContainer = Color(0xFFB9F6CA),
    secondary = Color(0xFFB8CCB3),
    background = Color(0xFF101410),
    surface = Color(0xFF101410),
    surfaceVariant = Color(0xFF414941),
)

/** Тема приложения: светлая и тёмная, переключается по системной настройке. */
@Composable
fun HabitTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}
