package com.fstengineering.leasilycrm.profile

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.repositories.UserRepository
import com.fstengineering.leasilycrm.core.domain.utils.ValidationError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onErrors
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.domain.validators.interfaces.UserValidator
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toUiMessage
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

        watchSessionExpiration()
        watchUser()

        isInitialized = true
    }

    private fun watchSessionExpiration() {
        viewModelScope.launch {
            userRepository.watchSessionExpiration().collect { result ->
                result
                    .onError { error ->
                        _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                    }
                    .onSuccess { hasSessionExpired ->
                        if (hasSessionExpired) _uiEvents.send(UiEvents.RedirectToSignIn)
                    }
            }
        }
    }

    private fun watchUser() {
        viewModelScope.launch {
            _isLoadingUser.update { true }

            userRepository.watchUser().collect { result ->
                result
                    .onError { error ->
                        if (error.isRedirectError()) _uiEvents.send(UiEvents.RedirectToSignIn)

                        _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                    }
                    .onSuccess { user ->
                        if (user.isNull()) return@onSuccess

                        _user.update { UserUiState.fromDomain(user) }
                    }

                _isLoadingUser.update { false }
            }
        }
    }

    fun editUser(editedUserState: UserUiState) {
        viewModelScope.launch {
            if (editedUserState == user.value) {
                _uiEvents.send(
                    UiEvents.EditUserSuccess(messageId = R.string.snackbar_no_data_changed)
                )
                return@launch
            }

            val editedUser = editedUserState.toDomain()

            userValidator.validate(editedUser)
                .onErrors { errors ->
                    val nameError = errors.firstOrNull { it is ValidationError.UserName }
                    val creciNumberError = errors.firstOrNull { it is ValidationError.CreciNumber }

                    _formErrors.update {
                        it.copy(
                            nameError = nameError?.toUiMessage(),
                            creciNumberError = creciNumberError?.toUiMessage(),
                        )
                    }

                    return@launch
                }

            userRepository.updateUser(user = editedUser)
                .onError { error ->
                    if (error.isRedirectError()) {
                        userRepository.deleteCachedUser()
                        _uiEvents.send(UiEvents.RedirectToSignIn)
                    }

                    _uiEvents.send(
                        UiEvents.DataSourceError(messageId = error.toUiMessage()),
                    )
                }
                .onSuccess {
                    _uiEvents.send(
                        UiEvents.EditUserSuccess(messageId = R.string.snackbar_edit_user_succeeded)
                    )
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

    fun signOutUser() {
        viewModelScope.launch {
            userRepository.signOutUser()
                .onError { error ->
                    _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                }
                .onSuccess {
                    userRepository.deleteCachedUser()
                    _uiEvents.send(UiEvents.RedirectToSignIn)
                }
        }
    }

    sealed interface UiEvents {
        data class DataSourceError(val messageId: Int) : UiEvents

        data class EditUserSuccess(val messageId: Int) : UiEvents

        data object RedirectToSignIn : UiEvents
    }
}
