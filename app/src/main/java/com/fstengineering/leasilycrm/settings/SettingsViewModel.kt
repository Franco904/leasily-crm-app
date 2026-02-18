package com.fstengineering.leasilycrm.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.domain.valueObjects.Settings
import com.fstengineering.leasilycrm.core.application.repositories.SettingsRepository
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
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

    private val _uiEvents = Channel<UiEvents>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _settings = MutableStateFlow(Settings())
    val settings = _settings.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    fun init() {
        if (isInitialized) return

        watchSettings()

        isInitialized = true
    }

    private fun watchSettings() {
        viewModelScope.launch {
            _isLoading.update { true }

            settingsRepository.watchSettings().collect { result ->
                result
                    .onError { e -> _uiEvents.send(UiEvents.Error(e)) }
                    .onSuccess { settings -> _settings.update { settings } }

                _isLoading.update { false }
            }
        }
    }

    fun onIsTodayCheckInCheckOutNotificationEnabledChange(isEnabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setIsTodayCheckInCheckOutNotificationEnabled(isEnabled = isEnabled)
                .onError { e -> _uiEvents.send(UiEvents.Error(e)) }
        }
    }

    sealed interface UiEvents {
        data class Error(val error: DataError) : UiEvents
    }
}
