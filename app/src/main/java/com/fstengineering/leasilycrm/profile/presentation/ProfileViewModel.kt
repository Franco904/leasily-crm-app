package com.fstengineering.leasilycrm.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.application.repositories.AuthRepository
import com.fstengineering.leasilycrm.core.application.repositories.UserRepository
import com.fstengineering.leasilycrm.core.application.services.interfaces.UserSessionService
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onErrors
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.domain.valueObjects.Creci
import com.fstengineering.leasilycrm.core.presentation.utils.onExternalError
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toInlineMessage
import com.fstengineering.leasilycrm.profile.application.models.UserCommandModel
import com.fstengineering.leasilycrm.profile.application.useCases.WriteUserUseCase
import com.fstengineering.leasilycrm.profile.presentation.models.ProfileUiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val writeUser: WriteUserUseCase,
    private val userSessionService: UserSessionService,
) : ViewModel() {
    private var isInitialized = false

    private val _uiEvents = Channel<UiEvent>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _creciFieldError = MutableStateFlow<Int?>(null)
    val creciFieldError = _creciFieldError.asStateFlow()

    fun init() {
        if (isInitialized) return

        watchUser()

        isInitialized = true
    }

    private fun watchUser() {
        viewModelScope.launch {
            userRepository.watchUser().collect { result ->
                result
                    .onExternalError { e -> _uiEvents.trySend(UiEvent.BottomSheetError(e)) }
                    .onSuccess { user -> _uiState.update { ProfileUiState.Loaded.fromDomain(user) } }
            }
        }
    }

    fun editUser(editedUiState: ProfileUiState.Loaded) {
        if (editedUiState == uiState.value) {
            _uiEvents.trySend(
                UiEvent.EditUserSuccess(messageId = R.string.no_data_changed_success_message)
            )
            return
        }

        val command = UserCommandModel(creciNumber = editedUiState.creciNumber)

        viewModelScope.launch {
            writeUser(command)
                .onExternalError { _uiEvents.send(UiEvent.BottomSheetError(it)) }
                .onErrors { errors ->
                    val domainError = errors.find { it is DomainError } as DomainError?
                    _creciFieldError.update { domainError?.toInlineMessage() }
                }
                .onSuccess {
                    val event =
                        UiEvent.EditUserSuccess(messageId = R.string.edit_user_creci_success_message)
                    _uiEvents.trySend(event)
                }
        }
    }

    fun onCreciNumberChange() {
        _creciFieldError.update { null }
    }

    fun onCreciFocusLoss(input: String?) {
        if (input == null) return

        Creci.parse(creciNumber = input)
            .onErrors { errors ->
                _creciFieldError.update { errors.firstOrNull()?.toInlineMessage() }
            }
    }

    fun clearErrors() {
        _creciFieldError.update { null }
    }

    fun onSignOut() {
        val event = authRepository.checkDeviceIsConnectedToInternet()
            .fold(
                onError = { e -> UiEvent.BottomSheetError(e) },
                onSuccess = { UiEvent.ContinueSignOut }
            )

        _uiEvents.trySend(event)
    }

    fun signOutUser() {
        viewModelScope.launch {
            userSessionService.onUserInitiatedSignOut()
                .onError { e -> _uiEvents.trySend(UiEvent.BottomSheetError(e)) }
                .onSuccess { _uiEvents.trySend(UiEvent.SignOutSuccess) }
        }
    }

    override fun onCleared() {
        _uiEvents.close()

        super.onCleared()
    }

    sealed interface UiEvent {
        data class BottomSheetError(val error: ApplicationError) : UiEvent
        data class EditUserSuccess(val messageId: Int) : UiEvent
        data object ContinueSignOut : UiEvent
        data object SignOutSuccess : UiEvent
    }
}
