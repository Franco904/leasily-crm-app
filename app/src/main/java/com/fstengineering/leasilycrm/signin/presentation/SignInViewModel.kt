package com.fstengineering.leasilycrm.signin.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.BaseError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.signin.application.models.commands.IdToken
import com.fstengineering.leasilycrm.signin.application.useCases.ChooseGoogleAccountUseCase
import com.fstengineering.leasilycrm.signin.application.useCases.SignInUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SignInViewModel(
    private val signIn: SignInUseCase,
    private val chooseGoogleAccount: ChooseGoogleAccountUseCase,
) : ViewModel() {
    private val _uiEvents = Channel<UiEvent>(Channel.BUFFERED)
    val uiEvents = _uiEvents.receiveAsFlow()

    fun onSignInSelect(mustEnableAutoSelect: Boolean) {
        viewModelScope.launch {
            val idToken = chooseGoogleAccount(mustEnableAutoSelect)
                .fold(
                    onError = { error ->
                        _uiEvents.trySend(
                            if (error == ApplicationError.UserCancellation) UiEvent.CloseSignInAttempt
                            else UiEvent.ShowError(error),
                        )

                        return@launch
                    },
                    onSuccess = { it },
                )

            signInUser(idToken)
        }
    }

    private suspend fun signInUser(idToken: String) {
        signIn(command = IdToken(idToken))
            .onError { error ->
                _uiEvents.trySend(UiEvent.ShowError(error))
            }
            .onSuccess { _uiEvents.trySend(UiEvent.FinishSignIn) }
    }

    sealed interface UiEvent {
        data class ShowError(val error: BaseError) : UiEvent
        data object CloseSignInAttempt : UiEvent
        data object FinishSignIn : UiEvent
    }
}
