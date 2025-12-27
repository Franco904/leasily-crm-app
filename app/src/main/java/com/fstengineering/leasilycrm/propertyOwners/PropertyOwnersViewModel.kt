package com.fstengineering.leasilycrm.propertyOwners

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.domain.models.PropertyOwner
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyOwnerRepository
import com.fstengineering.leasilycrm.core.domain.repositories.UserRepository
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toUiMessage
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PropertyOwnersViewModel(
    private val propertyOwnerRepository: PropertyOwnerRepository,
    private val userRepository: UserRepository,
) : ViewModel() {
    private var isInitialized = false

    private val _uiEvents = Channel<UiEvents>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _propertyOwners =
        MutableStateFlow<ImmutableList<PropertyOwner>>(persistentListOf())
    val propertyOwners = _propertyOwners.asStateFlow()

    private val _propertyOwnerToDelete = MutableStateFlow<PropertyOwner?>(null)
    val propertyOwnerToDelete = _propertyOwnerToDelete.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun init() {
        if (isInitialized) return

        watchSessionExpiration()
        watchPropertyOwners()

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

    private fun watchPropertyOwners() {
        _isLoading.update { true }

        viewModelScope.launch {
            propertyOwnerRepository.watchAll().collect { result ->
                result
                    .onError { error ->
                        _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                    }
                    .onSuccess { propertyOwners ->
                        _propertyOwners.update { propertyOwners }
                    }

                _isLoading.update { false }
            }
        }
    }

    fun onDeletePropertyOwner(
        propertyOwner: PropertyOwner?,
    ) {
        _propertyOwnerToDelete.update { propertyOwner }
    }

    fun deletePropertyOwner() {
        viewModelScope.launch {
            val id = propertyOwnerToDelete.value?.id ?: return@launch

            propertyOwnerRepository.delete(id)
                .onError { error ->
                    if (error.isRedirectError()) {
                        userRepository.deleteCachedUser()
                        _uiEvents.send(UiEvents.RedirectToSignIn)
                    }

                    _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))

                    return@launch
                }

            _uiEvents.send(UiEvents.PropertyOwnerDeleteSuccess)

            onDeletePropertyOwner(propertyOwner = null)
        }
    }

    sealed interface UiEvents {
        data class DataSourceError(val messageId: Int) : UiEvents

        data object RedirectToSignIn : UiEvents

        data object PropertyOwnerDeleteSuccess : UiEvents
    }
}
