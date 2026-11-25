package com.example.habittracker.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

/** Общая обработка ошибок: любое исключение превращается в сообщение для Snackbar. */
abstract class BaseViewModel : ViewModel() {

    private val _messages = MutableSharedFlow<String>(extraBufferCapacity = 4)
    val messages: SharedFlow<String> = _messages.asSharedFlow()

    protected fun showMessage(text: String) {
        _messages.tryEmit(text)
    }

    protected fun launchSafely(errorMessage: String, block: suspend () -> Unit): Job =
        viewModelScope.launch {
            try {
                block()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                showMessage(errorMessage)
            }
        }
}
