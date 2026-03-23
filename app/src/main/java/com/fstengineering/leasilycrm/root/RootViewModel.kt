package com.fstengineering.leasilycrm.root

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.application.stores.SettingsSharedStore
import com.fstengineering.leasilycrm.core.application.stores.UserSessionSharedStore
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class RootViewModel(
    private val userSessionSharedStore: UserSessionSharedStore,
    private val settingsSharedStore: SettingsSharedStore,
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
            val isSessionAlive = userSessionSharedStore.isSessionAlive()

            _uiEvents.send(UiEvents.SignInState(isSessionAlive))
        }
    }

    fun uncheckAppNotificationSettings() {
        viewModelScope.launch {
            settingsSharedStore
                .setIsTodayCheckInCheckOutNotificationEnabled(isEnabled = false)
        }
    }

    sealed interface UiEvents {
        data class SignInState(val isSessionAlive: Boolean) : UiEvents
    }
}
