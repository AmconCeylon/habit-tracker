package com.example.habittracker.ui.detail

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
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

data class HabitDetailUiState(
    val isLoading: Boolean = true,
    /** null после загрузки означает, что привычка не найдена (например, удалена). */
    val item: HabitWithStats? = null,
    val error: String? = null,
)

class HabitDetailViewModel(
    private val repository: HabitRepository,
    private val habitId: Long,
) : BaseViewModel() {

    val uiState: StateFlow<HabitDetailUiState> = repository.observeHabitWithStats(habitId)
        .map { HabitDetailUiState(isLoading = false, item = it) }
        .catch { emit(HabitDetailUiState(isLoading = false, error = "Не удалось загрузить данные")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HabitDetailUiState())

    /** Переключить отметку за конкретный день (в том числе исправить пропущенный). */
    fun toggleDay(date: LocalDate, currentlyDone: Boolean) {
        if (date.isAfter(LocalDate.now())) return
        launchSafely("Не удалось сохранить отметку") {
            repository.setCompleted(habitId, date, !currentlyDone)
        }
    }

    companion object {
        const val ARG_HABIT_ID = "habitId"

        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as HabitApp
                val id = createSavedStateHandle().get<Long>(ARG_HABIT_ID) ?: -1L
                HabitDetailViewModel(app.container.repository, id)
            }
        }
    }
}
