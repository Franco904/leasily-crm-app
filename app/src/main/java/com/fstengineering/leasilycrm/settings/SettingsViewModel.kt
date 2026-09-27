package com.fstengineering.leasilycrm.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.application.stores.SettingsSharedStore
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.settings.models.SettingsUiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsSharedStore: SettingsSharedStore,
) : ViewModel() {
    private var isInitialized = false

    private val _uiEvents = Channel<UiEvent>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _settingsUiState = MutableStateFlow<SettingsUiState>(SettingsUiState.Loading)
    val settingsUiState = _settingsUiState.asStateFlow()

    fun init() {
        if (isInitialized) return

        watchSettings()

        isInitialized = true
    }

    private fun watchSettings() {
        viewModelScope.launch {
            settingsSharedStore.watch().collect { settings ->
                _settingsUiState.update { SettingsUiState.Loaded.fromReadModel(settings) }
            }
        }
    }

    fun onIsTodayCheckInCheckOutNotificationEnabledChange(isEnabled: Boolean) {
        viewModelScope.launch {
            settingsSharedStore.setIsTodayCheckInCheckOutNotificationEnabled(isEnabled = isEnabled)
        }
    }

    override fun onCleared() {
        _uiEvents.close()

        super.onCleared()
    }

    sealed interface UiEvent {
        data class Error(val error: ApplicationError) : UiEvent
    }
}
