package com.fstengineering.leasilycrm.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.domain.repositories.UserRepository
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.presentation.external.models.GoogleUserCredentials
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SignInViewModel(
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _uiEvents = Channel<UiEvents>()
    val uiEvents = _uiEvents.receiveAsFlow()

    fun onSignIn() {
        viewModelScope.launch {
            val event = if (userRepository.isDeviceConnectedToInternet()) UiEvents.ContinueSignIn
            else UiEvents.Error(DataError.NoInternetError)

            _uiEvents.send(event)
        }
    }

    fun signInUser(credentials: GoogleUserCredentials) {
        viewModelScope.launch {
            userRepository.signInUser(
                idToken = credentials.idToken,
                user = credentials.toUser(),
            )
                .onError { e -> _uiEvents.send(UiEvents.Error(e)) }
                .onSuccess { _uiEvents.send(UiEvents.SignInSuccess) }
        }
    }

    sealed interface UiEvents {
        data class Error(val error: DataError) : UiEvents
        data object ContinueSignIn : UiEvents
        data object SignInSuccess : UiEvents
    }
}
