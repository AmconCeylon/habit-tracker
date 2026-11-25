package com.example.habittracker.ui.edit

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.habittracker.HabitApp
import com.example.habittracker.data.HabitRepository
import com.example.habittracker.domain.Habit
import com.example.habittracker.domain.HabitValidator
import com.example.habittracker.ui.BaseViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate

data class HabitEditUiState(
    val isLoading: Boolean = false,
    val isEditing: Boolean = false,
    val notFound: Boolean = false,
    val name: String = "",
    val description: String = "",
    val reminderEnabled: Boolean = false,
    val reminderHour: Int = 9,
    val reminderMinute: Int = 0,
    val nameError: HabitValidator.NameError? = null,
)

/** Одноразовые события экрана. */
enum class EditEvent { SAVED, DELETED }

class HabitEditViewModel(
    private val repository: HabitRepository,
    private val habitId: Long?,
) : BaseViewModel() {

    private val _state = MutableStateFlow(
        HabitEditUiState(isLoading = habitId != null, isEditing = habitId != null),
    )
    val state: StateFlow<HabitEditUiState> = _state.asStateFlow()

    private val _events = Channel<EditEvent>(Channel.BUFFERED)
    val events: Flow<EditEvent> = _events.receiveAsFlow()

    private var original: Habit? = null

    init {
        if (habitId != null) {
            launchSafely("Не удалось загрузить привычку") {
                val habit = repository.getHabit(habitId)
                if (habit == null) {
                    _state.update { it.copy(isLoading = false, notFound = true) }
                } else {
                    original = habit
                    _state.update {
                        it.copy(
                            isLoading = false,
                            name = habit.name,
                            description = habit.description,
                            reminderEnabled = habit.reminderEnabled,
                            reminderHour = habit.reminderHour,
                            reminderMinute = habit.reminderMinute,
                        )
                    }
                }
            }
        }
    }

    fun onNameChange(value: String) = _state.update { it.copy(name = value, nameError = null) }

    fun onDescriptionChange(value: String) =
        _state.update { it.copy(description = value.take(HabitValidator.MAX_DESCRIPTION_LENGTH)) }

    fun onReminderEnabledChange(enabled: Boolean) = _state.update { it.copy(reminderEnabled = enabled) }

    fun onReminderTimeChange(hour: Int, minute: Int) =
        _state.update { it.copy(reminderHour = hour, reminderMinute = minute) }

    fun save() {
        val current = _state.value
        val error = HabitValidator.validateName(current.name)
        if (error != null) {
            _state.update { it.copy(nameError = error) }
            return
        }
        launchSafely("Не удалось сохранить привычку") {
            repository.saveHabit(
                Habit(
                    id = original?.id ?: 0L,
                    name = current.name,
                    description = current.description,
                    reminderEnabled = current.reminderEnabled,
                    reminderHour = current.reminderHour,
                    reminderMinute = current.reminderMinute,
                    createdOn = original?.createdOn ?: LocalDate.now(),
                ),
            )
            _events.send(EditEvent.SAVED)
        }
    }

    fun delete() {
        val habit = original ?: return
        launchSafely("Не удалось удалить привычку") {
            repository.deleteHabit(habit)
            _events.send(EditEvent.DELETED)
        }
    }

    companion object {
        const val ARG_HABIT_ID = "habitId"

        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as HabitApp
                val handle = createSavedStateHandle()
                val id = handle.get<Long>(ARG_HABIT_ID)?.takeIf { it >= 0 }
                HabitEditViewModel(app.container.repository, id)
            }
        }
    }
}
