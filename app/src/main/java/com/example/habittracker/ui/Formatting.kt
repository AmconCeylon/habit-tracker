package com.example.habittracker.ui

/** «1 день подряд», «2 дня подряд», «5 дней подряд». */
fun streakLabel(days: Int): String {
    val mod100 = days % 100
    val mod10 = days % 10
    val word = when {
        mod100 in 11..14 -> "дней"
        mod10 == 1 -> "день"
        mod10 in 2..4 -> "дня"
        else -> "дней"
    }
    return "$days $word подряд"
}
