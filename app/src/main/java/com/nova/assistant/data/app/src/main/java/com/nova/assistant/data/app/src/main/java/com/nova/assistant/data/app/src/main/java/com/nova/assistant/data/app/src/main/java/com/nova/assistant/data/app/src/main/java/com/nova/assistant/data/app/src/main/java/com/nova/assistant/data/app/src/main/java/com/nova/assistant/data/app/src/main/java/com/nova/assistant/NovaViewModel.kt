
package com.nova.assistant

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nova.assistant.data.ChatMessage
import com.nova.assistant.data.ChatRepository
import com.nova.assistant.data.NovaDatabase
import com.nova.assistant.data.SecureSettings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class NovaViewModel(application: Application) : AndroidViewModel(application) {

    private val database = NovaDatabase.getDatabase(application)
    private val settings = SecureSettings(application)
    private val repository = ChatRepository(database.chatDao(), settings)

    val messages: StateFlow<List<ChatMessage>> =
        repository.messages.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun sendMessage(text: String) {
        if (_isLoading.value || text.isBlank()) return

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                repository.sendMessage(text)
            } catch (e: Exception) {
                _error.value = e.message ?: "Something went wrong"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearChat() {
        viewModelScope.launch {
            repository.clearChat()
            _error.value = null
        }
    }

    fun dismissError() {
        _error.value = null
    }

    fun saveConnection(url: String, token: String) {
        try {
            settings.backendUrl = url.trim()
            settings.accessToken = token.trim()
            _error.value = null
        } catch (e: Exception) {
            _error.value = e.message ?: "Could not save settings"
        }
    }
}
