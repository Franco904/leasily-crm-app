package com.fstengineering.leasilycrm.signin

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.domain.repositories.UserRepository
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.presentation.external.models.GoogleUserCredentials
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toUiMessage
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SignInViewModel(
    private val userRepository: UserRepository,
) : ViewModel() {
    private var isInitialized = false

    private val _uiEvents = Channel<UiEvents>()
    val uiEvents = _uiEvents.receiveAsFlow()

    var didUserExplicitlySignOut = false

    fun init() {
        if (isInitialized) return

        checkUserDidExplicitlySignOut()

        isInitialized = true
    }

    private fun checkUserDidExplicitlySignOut() {
        viewModelScope.launch {
            userRepository.didUserExplicitlySignOut()
                .onError { error ->
                    _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                }
                .onSuccess { didExplicitlySignOut ->
                    didUserExplicitlySignOut = didExplicitlySignOut
                }
        }
    }

    fun onSignInUser(credentials: GoogleUserCredentials) {
        viewModelScope.launch {
            userRepository.signInUser(
                idToken = credentials.idToken,
                user = credentials.toUser(),
            )
                .onError { error ->
                    _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                }
                .onSuccess {
                    _uiEvents.send(UiEvents.SignInSuccess)
                }
        }
    }

    sealed interface UiEvents {
        data class DataSourceError(val messageId: Int) : UiEvents

        data object SignInSuccess : UiEvents
    }
}
