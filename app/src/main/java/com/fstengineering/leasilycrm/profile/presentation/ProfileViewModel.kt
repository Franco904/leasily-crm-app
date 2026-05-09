package com.fstengineering.leasilycrm.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.application.services.interfaces.ConnectivityService
import com.fstengineering.leasilycrm.core.application.services.interfaces.UserSessionService
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.presentation.utils.onApplicationError
import com.fstengineering.leasilycrm.core.presentation.utils.onDomainErrors
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toInlineMessage
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.CommandUseCase
import com.fstengineering.leasilycrm.profile.application.models.commands.UserCommandModel
import com.fstengineering.leasilycrm.profile.application.repositories.UserQueryRepository
import com.fstengineering.leasilycrm.profile.domain.valueObjects.Creci
import com.fstengineering.leasilycrm.profile.presentation.models.ProfileUiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val writeUser: CommandUseCase<UserCommandModel>,
    private val connectivityService: ConnectivityService,
    private val userSessionService: UserSessionService,
    private val userQueryRepository: UserQueryRepository,
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
            userQueryRepository.watch().collect { result ->
                result
                    .onApplicationError { e -> _uiEvents.trySend(UiEvent.BottomSheetError(e)) }
                    .onSuccess { user -> _uiState.update { ProfileUiState.Loaded.fromReadModel(user) } }
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
                .onApplicationError { _uiEvents.send(UiEvent.BottomSheetError(it)) }
                .onDomainErrors { errors ->
                    _creciFieldError.update { errors.firstOrNull()?.toInlineMessage() }
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
            .onError { e -> _creciFieldError.update { e.toInlineMessage() } }
    }

    fun clearErrors() {
        _creciFieldError.update { null }
    }

    fun onSignOut() {
        val event = connectivityService.checkDeviceIsConnectedToInternet()
            .fold(
                onError = { e -> UiEvent.BottomSheetError(e) },
                onSuccess = { UiEvent.ContinueSignOut }
            )

        _uiEvents.trySend(event)
    }

    fun signOutUser() {
        viewModelScope.launch {
            userSessionService.onUserInitiatedSignOut()

            _uiEvents.trySend(UiEvent.SignOutSuccess)
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
