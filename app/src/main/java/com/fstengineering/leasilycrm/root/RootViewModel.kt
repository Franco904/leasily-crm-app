package com.fstengineering.leasilycrm.root

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.domain.repositories.UserRepository
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toUiMessage
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class RootViewModel(
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
                .onError { error ->
                    _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                }
                .onSuccess { isUserSignedIn ->
                    _uiEvents.send(UiEvents.SignInState(isUserSignedIn))
                }
        }
    }

    sealed interface UiEvents {
        data class DataSourceError(@StringRes val messageId: Int) : UiEvents

        data class SignInState(val isUserSignedIn: Boolean) : UiEvents
    }
}
