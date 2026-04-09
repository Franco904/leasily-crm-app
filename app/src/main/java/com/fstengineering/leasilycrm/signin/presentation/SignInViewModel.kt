package com.fstengineering.leasilycrm.signin.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.application.services.interfaces.ConnectivityService
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.presentation.utils.onApplicationError
import com.fstengineering.leasilycrm.signin.application.models.commands.IdToken
import com.fstengineering.leasilycrm.signin.application.useCases.SignInUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SignInViewModel(
    private val signIn: SignInUseCase,
    private val connectivityService: ConnectivityService,
) : ViewModel() {
    private val _uiEvents = Channel<UiEvent>()
    val uiEvents = _uiEvents.receiveAsFlow()

    fun onSignIn() {
        viewModelScope.launch {
            val event = connectivityService.checkDeviceIsConnectedToInternet()
                .fold(
                    onError = { e -> UiEvent.BottomSheetError(e) },
                    onSuccess = { UiEvent.ContinueSignIn }
                )

            _uiEvents.send(event)
        }
    }

    fun signInUser(idToken: String) {
        viewModelScope.launch {
            signIn(command = IdToken(idToken))
                .onApplicationError { e -> _uiEvents.trySend(UiEvent.BottomSheetError(e)) }
                .onSuccess { _uiEvents.trySend(UiEvent.SignInSuccess) }
        }
    }

    override fun onCleared() {
        _uiEvents.close()

        super.onCleared()
    }

    sealed interface UiEvent {
        data class BottomSheetError(val error: ApplicationError) : UiEvent
        data object ContinueSignIn : UiEvent
        data object SignInSuccess : UiEvent
    }
}
