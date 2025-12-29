package com.fstengineering.leasilycrm.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.repositories.UserRepository
import com.fstengineering.leasilycrm.core.domain.services.interfaces.UserSessionService
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onErrors
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.domain.validators.interfaces.UserValidator
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toInlineMessage
import com.fstengineering.leasilycrm.profile.models.EditUserErrorUiState
import com.fstengineering.leasilycrm.profile.models.UserUiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userValidator: UserValidator,
    private val userRepository: UserRepository,
    private val userSessionService: UserSessionService,
) : ViewModel() {
    private var isInitialized = false

    private val _uiEvents = Channel<UiEvents>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _user = MutableStateFlow(UserUiState())
    val user = _user.asStateFlow()

    private val _formErrors = MutableStateFlow(EditUserErrorUiState())
    val formErrors = _formErrors.asStateFlow()

    private val _isLoadingUser = MutableStateFlow(true)
    val isLoadingUser = _isLoadingUser.asStateFlow()

    fun init() {
        if (isInitialized) return

        watchUser()

        isInitialized = true
    }

    private fun watchUser() {
        viewModelScope.launch {
            _isLoadingUser.update { true }

            userRepository.watchUser().collect { result ->
                result
                    .onError { e -> _uiEvents.send(UiEvents.Error(e)) }
                    .onSuccess { user ->
                        if (!user.isNull()) _user.update { UserUiState.fromDomain(user) }
                    }

                _isLoadingUser.update { false }
            }
        }
    }

    fun editUser(editedUserState: UserUiState) {
        viewModelScope.launch {
            if (editedUserState == user.value) {
                _uiEvents.send(
                    UiEvents.EditUserSuccess(messageId = R.string.no_data_changed_success_message)
                )
                return@launch
            }

            val editedUser = editedUserState.toDomain()

            userValidator.validate(editedUser)
                .onErrors { errors ->
                    val nameError = errors.firstOrNull { it is DomainError.UserName }
                    val creciNumberError = errors.firstOrNull { it is DomainError.CreciNumber }

                    _formErrors.update {
                        it.copy(
                            nameError = nameError?.toInlineMessage(),
                            creciNumberError = creciNumberError?.toInlineMessage(),
                        )
                    }

                    return@launch
                }

            userRepository.updateUser(user = editedUser)
                .onError { e -> _uiEvents.send(UiEvents.Error(e)) }
                .onSuccess {
                    val event =
                        UiEvents.EditUserSuccess(messageId = R.string.edit_user_success_message)
                    _uiEvents.send(event)
                }
        }
    }

    fun onUserChange(newUserData: UserUiState) {
        if (newUserData.name != user.value.name) {
            _formErrors.update {
                it.copy(nameError = null)
            }
        }

        if (newUserData.creciNumber != user.value.creciNumber) {
            _formErrors.update {
                it.copy(creciNumberError = null)
            }
        }
    }

    fun clearErrors() {
        _formErrors.update { EditUserErrorUiState() }
    }

    fun onSignOut() {
        viewModelScope.launch {
            val event = if (userRepository.isDeviceConnectedToInternet()) UiEvents.ContinueSignOut
            else UiEvents.Error(DataError.NoInternetError)

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
        data class Error(val error: DataError) : UiEvents
        data class EditUserSuccess(val messageId: Int) : UiEvents
        data object ContinueSignOut : UiEvents
        data object SignOutSuccess : UiEvents
    }
}
