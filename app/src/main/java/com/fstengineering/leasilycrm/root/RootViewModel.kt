package com.fstengineering.leasilycrm.root

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.domain.repositories.SettingsRepository
import com.fstengineering.leasilycrm.core.domain.repositories.UserRepository
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class RootViewModel(
    private val settingsRepository: SettingsRepository,
    private val userRepository: UserRepository,
) : ViewModel() {
    private var isInitialized = false

    private val _uiEvents = Channel<UiEvents>()
    val uiEvents = _uiEvents.receiveAsFlow()

    fun init() {
        if (isInitialized) return

        checkIsUserSignedIn()

        isInitialized = true
    }

    private fun checkIsUserSignedIn() {
        viewModelScope.launch {
            userRepository.isUserSignedIn()
                .onSuccess { isUserSignedIn ->
                    _uiEvents.send(UiEvents.SignInState(isUserSignedIn))
                }
        }
    }

    fun uncheckAppNotificationSettings() {
        viewModelScope.launch {
            settingsRepository.setIsTodayCheckInCheckOutNotificationEnabled(isEnabled = false)
        }
    }

    sealed interface UiEvents {
        data class SignInState(val isUserSignedIn: Boolean) : UiEvents
    }
}
