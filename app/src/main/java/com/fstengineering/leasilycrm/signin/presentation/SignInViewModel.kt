package com.fstengineering.leasilycrm.signin.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.ValidationErrors
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.signin.application.models.commands.IdToken
import com.fstengineering.leasilycrm.signin.application.useCases.SignInUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SignInViewModel(
    private val signIn: SignInUseCase,
) : ViewModel() {
    private val _uiEvents = Channel<UiEvent>()
    val uiEvents = _uiEvents.receiveAsFlow()

    fun signInUser(idToken: String) {
        viewModelScope.launch {
            signIn(command = IdToken(idToken))
                .onError { error ->
                    when (error) {
                        is ApplicationError -> _uiEvents.trySend(UiEvent.BottomSheetError(error))
                        is ValidationErrors, is DomainError -> Unit
                    }
                }
                .onSuccess { _uiEvents.trySend(UiEvent.SignInSuccess) }
        }
    }

    override fun onCleared() {
        _uiEvents.close()

        super.onCleared()
    }

    sealed interface UiEvent {
        data class BottomSheetError(val error: ApplicationError) : UiEvent
        data object SignInSuccess : UiEvent
    }
}
