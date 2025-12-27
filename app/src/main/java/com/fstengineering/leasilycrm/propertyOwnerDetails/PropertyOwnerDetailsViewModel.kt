package com.fstengineering.leasilycrm.propertyOwnerDetails

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.domain.models.PropertyOwner
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyOwnerRepository
import com.fstengineering.leasilycrm.core.domain.repositories.UserRepository
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toUiMessage
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PropertyOwnerDetailsViewModel(
    private val propertyOwnerRepository: PropertyOwnerRepository,
    private val userRepository: UserRepository,
) : ViewModel() {
    private var isInitialized = false

    private val _uiEvents = Channel<UiEvents>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _propertyOwner = MutableStateFlow(PropertyOwner())
    val propertyOwner = _propertyOwner.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private var watchPropertyOwnerJob: Job? = null

    fun init(propertyOwnerId: Long) {
        if (isInitialized) return

        watchSessionExpiration()
        watchPropertyOwner(propertyOwnerId = propertyOwnerId)

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

    private fun watchPropertyOwner(propertyOwnerId: Long) {
        _isLoading.update { true }

        watchPropertyOwnerJob = viewModelScope.launch {
            propertyOwnerRepository.watchById(propertyOwnerId).collect { result ->
                result
                    .onError { error ->
                        _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                    }
                    .onSuccess { storedPropertyOwner ->
                        _propertyOwner.update { storedPropertyOwner }
                    }

                _isLoading.update { false }
            }
        }
    }

    fun deletePropertyOwner() {
        val propertyOwnerId = propertyOwner.value.id ?: return

        watchPropertyOwnerJob?.cancel()

        viewModelScope.launch {
            propertyOwnerRepository.delete(id = propertyOwnerId)
                .onError { error ->
                    if (error.isRedirectError()) {
                        userRepository.deleteCachedUser()
                        _uiEvents.send(UiEvents.RedirectToSignIn)
                    }

                    _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                    return@launch
                }

            _uiEvents.send(UiEvents.PropertyOwnerDeleteSuccess)
        }
    }

    sealed interface UiEvents {
        data class DataSourceError(val messageId: Int) : UiEvents

        data object RedirectToSignIn : UiEvents

        data object PropertyOwnerDeleteSuccess : UiEvents
    }
}
