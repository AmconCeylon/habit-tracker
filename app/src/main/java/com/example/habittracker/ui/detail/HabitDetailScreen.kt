package com.example.habittracker.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habittracker.domain.HabitWithStats
import com.example.habittracker.ui.streakLabel
import java.time.LocalDate

private const val CALENDAR_DAYS = 28
private const val DAYS_IN_ROW = 7

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitDetailScreen(
    onBack: () -> Unit,
    onEdit: (Long) -> Unit,
    viewModel: HabitDetailViewModel = viewModel(factory = HabitDetailViewModel.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHost = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.messages.collect { snackbarHost.showSnackbar(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.item?.habit?.name ?: "Привычка") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    state.item?.let { item ->
                        IconButton(onClick = { onEdit(item.habit.id) }) {
                            Icon(Icons.Filled.Edit, contentDescription = "Редактировать")
                        }
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHost) },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            val item = state.item
            when {
                state.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                state.error != null -> CenteredText(state.error.orEmpty())
                item == null -> CenteredText("Привычка не найдена")
                else -> DetailContent(item, onToggleDay = viewModel::toggleDay)
            }
        }
    }
}

@Composable
private fun BoxScope.CenteredText(text: String) {
    Text(
        text,
        modifier = Modifier.align(Alignment.Center).padding(32.dp),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.titleMedium,
    )
}

@Composable
private fun DetailContent(item: HabitWithStats, onToggleDay: (LocalDate, Boolean) -> Unit) {
    val stats = item.stats
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (item.habit.description.isNotBlank()) {
            Text(item.habit.description, style = MaterialTheme.typography.bodyLarge)
        }
        Text(
            "🔥 ${streakLabel(stats.currentStreak)}",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard("Лучшая серия", "${stats.bestStreak}", Modifier.weight(1f))
            StatCard("Всего выполнено", "${stats.totalCompletions}", Modifier.weight(1f))
            StatCard("За 30 дней", "${stats.completionRatePercent}%", Modifier.weight(1f))
        }
        if (item.habit.reminderEnabled) {
            Text(
                "Напоминание каждый день в %02d:%02d".format(item.habit.reminderHour, item.habit.reminderMinute),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Text("Последние $CALENDAR_DAYS дней", style = MaterialTheme.typography.titleMedium)
        Text(
            "Нажмите на день, чтобы изменить отметку",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        val today = LocalDate.now()
        val days = (CALENDAR_DAYS - 1 downTo 0).map { today.minusDays(it.toLong()) }
        days.chunked(DAYS_IN_ROW).forEach { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                week.forEach { date ->
                    DayCell(date = date, done = date in item.completedDates, onClick = onToggleDay)
                }
            }
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, style = MaterialTheme.typography.headlineSmall)
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun DayCell(date: LocalDate, done: Boolean, onClick: (LocalDate, Boolean) -> Unit) {
    val background = if (done) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (done) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    Box(
        modifier = Modifier
            .size(40.dp)
            .background(background, CircleShape)
            .clickable { onClick(date, done) },
        contentAlignment = Alignment.Center,
    ) {
        Text("${date.dayOfMonth}", color = textColor, style = MaterialTheme.typography.labelLarge)
    }
}
