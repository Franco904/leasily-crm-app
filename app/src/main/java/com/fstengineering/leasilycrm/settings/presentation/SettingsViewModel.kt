package com.fstengineering.leasilycrm.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.settings.application.repositories.SettingsRepository
import com.fstengineering.leasilycrm.settings.presentation.models.SettingsUiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
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
            settingsRepository.watch().collect { result ->
                result
                    .onError { e -> _uiEvents.send(UiEvent.Error(e)) }
                    .onSuccess { settings ->
                        _settingsUiState.update { SettingsUiState.Loaded.fromReadModel(settings) }
                    }
            }
        }
    }

    fun onIsTodayCheckInCheckOutNotificationEnabledChange(isEnabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setIsTodayCheckInCheckOutNotificationEnabled(isEnabled = isEnabled)
                .onError { e -> _uiEvents.send(UiEvent.Error(e)) }
        }
    }

    sealed interface UiEvent {
        data class Error(val error: ApplicationError) : UiEvent
    }
}
