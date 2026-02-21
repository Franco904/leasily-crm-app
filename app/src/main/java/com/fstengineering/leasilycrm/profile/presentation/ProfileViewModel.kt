package com.fstengineering.leasilycrm.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.application.repositories.AuthRepository
import com.fstengineering.leasilycrm.core.application.repositories.UserRepository
import com.fstengineering.leasilycrm.core.application.services.interfaces.UserSessionService
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.UseCase
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toInlineMessage
import com.fstengineering.leasilycrm.profile.application.models.UserWriteModel
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
    private val updateUserCreciNumber: UseCase<UserWriteModel, Unit>,
    private val userSessionService: UserSessionService,
) : ViewModel() {
    private var isInitialized = false

    private val _uiEvents = Channel<ProfileUiEvent>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Empty)
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
                    .onError { e -> _uiEvents.send(ProfileUiEvent.Error(e)) }
                    .onSuccess { user -> _uiState.update { ProfileUiState.Loaded.fromDomain(user) } }
            }
        }
    }

    fun editUser(editedUiState: ProfileUiState.Loaded) {
        viewModelScope.launch {
            if (editedUiState == uiState.value) {
                _uiEvents.send(
                    ProfileUiEvent.EditUserSuccess(messageId = R.string.no_data_changed_success_message)
                )
                return@launch
            }

            val useCaseParams =
                UserWriteModel(creciNumber = editedUiState.creciNumber)

            updateUserCreciNumber(useCaseParams)
                .onError { e ->
                    when (e) {
                        is ApplicationError -> _uiEvents.send(ProfileUiEvent.Error(e))
                        is DomainError -> _creciFieldError.update { e.toInlineMessage() }
                    }
                }
                .onSuccess {
                    val event =
                        ProfileUiEvent.EditUserSuccess(messageId = R.string.edit_user_creci_success_message)
                    _uiEvents.send(event)
                }
        }
    }

    fun onCreciNumberChange() {
        _creciFieldError.update { null }
    }

    fun clearErrors() {
        _creciFieldError.update { null }
    }

    fun onSignOut() {
        viewModelScope.launch {
            val event = authRepository.checkDeviceIsConnectedToInternet()
                .fold(
                    onError = { e -> ProfileUiEvent.Error(e) },
                    onSuccess = { ProfileUiEvent.ContinueSignOut }
                )

            _uiEvents.send(event)
        }
    }

    fun signOutUser() {
        viewModelScope.launch {
            userSessionService.onUserInitiatedSignOut()
                .onError { e -> _uiEvents.send(ProfileUiEvent.Error(e)) }
                .onSuccess { _uiEvents.send(ProfileUiEvent.SignOutSuccess) }
        }
    }

    sealed interface ProfileUiEvent {
        data class Error(val error: ApplicationError) : ProfileUiEvent
        data class EditUserSuccess(val messageId: Int) : ProfileUiEvent
        data object ContinueSignOut : ProfileUiEvent
        data object SignOutSuccess : ProfileUiEvent
    }
}
