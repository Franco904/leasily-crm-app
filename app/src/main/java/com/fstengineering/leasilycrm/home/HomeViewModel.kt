package com.fstengineering.leasilycrm.home

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.domain.models.Assistance
import com.fstengineering.leasilycrm.core.domain.models.Tenant
import com.fstengineering.leasilycrm.core.domain.repositories.UserRepository
import com.fstengineering.leasilycrm.core.domain.useCases.interfaces.DeleteCurrentAssistanceUseCase
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toUiMessage
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val deleteCurrentAssistanceUseCase: DeleteCurrentAssistanceUseCase,
    private val userRepository: UserRepository,
) : ViewModel() {
    private var isInitialized = false

    private val _uiEvents = Channel<UiEvents>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _userProfilePictureUrl = MutableStateFlow<String?>(null)
    val userProfilePictureUrl = _userProfilePictureUrl.asStateFlow()

    fun init() {
        if (isInitialized) return

        watchSessionExpiration()
        watchUserProfilePictureUrl()

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
                        if (hasSessionExpired) {
                            userRepository.deleteCachedUser()
                            _uiEvents.send(UiEvents.RedirectToSignIn)
                        }
                    }
            }
        }
    }

    fun watchUserProfilePictureUrl() {
        viewModelScope.launch {
            userRepository.watchUser().collect { result ->
                result
                    .onError { error ->
                        if (error.isRedirectError()) {
                            userRepository.deleteCachedUser()
                            _uiEvents.send(UiEvents.RedirectToSignIn)
                        }

                        _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                    }
                    .onSuccess { user ->
                        _userProfilePictureUrl.value = user.profilePictureUrl
                    }
            }
        }
    }

    fun deleteCurrentAssistance(
        currentAssistance: Assistance,
        leadTenant: Tenant,
    ) {
        viewModelScope.launch {
            deleteCurrentAssistanceUseCase(
                currentAssistance = currentAssistance,
                hasFinishedStays = leadTenant.hasFinishedStays(),
            )
                .onError { error ->
                    if (error.isRedirectError()) {
                        userRepository.deleteCachedUser()
                        _uiEvents.send(UiEvents.RedirectToSignIn)
                    }

                    _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                    return@launch
                }

            _uiEvents.send(UiEvents.CurrentAssistanceDeleteSuccess)
        }
    }

    sealed interface UiEvents {
        data class DataSourceError(@StringRes val messageId: Int) : UiEvents

        data object RedirectToSignIn : UiEvents

        data object CurrentAssistanceDeleteSuccess : UiEvents
    }
}
