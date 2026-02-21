package com.fstengineering.leasilycrm.profile

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
import com.fstengineering.leasilycrm.profile.models.UserUiState
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

    private val _uiEvents = Channel<UiEvents>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _user = MutableStateFlow<UserUiState>(UserUiState.Empty)
    val user = _user.asStateFlow()

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
                    .onError { e -> _uiEvents.send(UiEvents.Error(e)) }
                    .onSuccess { user -> _user.update { UserUiState.Loaded.fromDomain(user) } }
            }
        }
    }

    fun editUser(editedUserState: UserUiState.Loaded) {
        viewModelScope.launch {
            if (editedUserState == user.value) {
                _uiEvents.send(
                    UiEvents.EditUserSuccess(messageId = R.string.no_data_changed_success_message)
                )
                return@launch
            }

            val useCaseParams =
                UserWriteModel(creciNumber = editedUserState.creciNumber)

            updateUserCreciNumber(useCaseParams)
                .onError { e ->
                    when (e) {
                        is ApplicationError -> _uiEvents.send(UiEvents.Error(e))
                        is DomainError -> _creciFieldError.update { e.toInlineMessage() }
                    }
                }
                .onSuccess {
                    val event =
                        UiEvents.EditUserSuccess(messageId = R.string.edit_user_creci_success_message)
                    _uiEvents.send(event)
                }
        }
    }

    fun onCreciNumberChange(newUserData: UserUiState.Loaded) {
        val currentUser = user.value
        if (currentUser !is UserUiState.Loaded) return

        if (newUserData.creciNumber != currentUser.creciNumber) {
            _creciFieldError.update { null }
        }
    }

    fun clearErrors() {
        _creciFieldError.update { null }
    }

    fun onSignOut() {
        viewModelScope.launch {
            val event = authRepository.checkDeviceIsConnectedToInternet()
                .fold(
                    onError = { e -> UiEvents.Error(e) },
                    onSuccess = { UiEvents.ContinueSignOut }
                )

            _uiEvents.send(event)
        }
    }

    fun signOutUser() {
        viewModelScope.launch {
            userSessionService.onUserInitiatedSignOut()
                .onError { e -> _uiEvents.send(UiEvents.Error(e)) }
                .onSuccess { _uiEvents.send(UiEvents.SignOutSuccess) }
        }
    }

    sealed interface UiEvents {
        data class Error(val error: ApplicationError) : UiEvents
        data class EditUserSuccess(val messageId: Int) : UiEvents
        data object ContinueSignOut : UiEvents
        data object SignOutSuccess : UiEvents
    }
}
