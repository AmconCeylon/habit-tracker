package com.example.habittracker.ui.list

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.habittracker.HabitApp
import com.example.habittracker.data.HabitRepository
import com.example.habittracker.domain.HabitWithStats
import com.example.habittracker.ui.BaseViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

data class HabitListUiState(
    val isLoading: Boolean = true,
    val habits: List<HabitWithStats> = emptyList(),
    val error: String? = null,
) {
    val doneToday: Int get() = habits.count { it.stats.doneToday }
}

class HabitListViewModel(private val repository: HabitRepository) : BaseViewModel() {

    val uiState: StateFlow<HabitListUiState> = repository.observeHabitsWithStats()
        .map { HabitListUiState(isLoading = false, habits = it) }
        .catch { emit(HabitListUiState(isLoading = false, error = "Не удалось загрузить привычки")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HabitListUiState())

    fun toggleToday(item: HabitWithStats) {
        launchSafely("Не удалось сохранить отметку") {
            repository.setCompleted(item.habit.id, LocalDate.now(), !item.stats.doneToday)
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as HabitApp
                HabitListViewModel(app.container.repository)
            }
        }
    }
}
