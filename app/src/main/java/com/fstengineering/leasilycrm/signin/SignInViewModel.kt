package com.fstengineering.leasilycrm.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.domain.repositories.AuthRepository
import com.fstengineering.leasilycrm.core.domain.repositories.UserRepository
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SignInViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _uiEvents = Channel<UiEvents>()
    val uiEvents = _uiEvents.receiveAsFlow()

    fun onSignIn() {
        viewModelScope.launch {
            val event = authRepository.checkDeviceIsConnectedToInternet()
                .fold(
                    onError = { e -> UiEvents.Error(e) },
                    onSuccess = { UiEvents.ContinueSignIn }
                )

            _uiEvents.send(event)
        }
    }

    fun signInUser(idToken: String) {
        viewModelScope.launch {
            val user = authRepository.signIn(idToken = idToken)
                .fold(
                    onError = { e -> return@launch _uiEvents.send(UiEvents.Error(e)) },
                    onSuccess = { it }
                )

            userRepository.storeUser(user)
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
