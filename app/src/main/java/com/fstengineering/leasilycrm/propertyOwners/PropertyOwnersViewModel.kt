package com.fstengineering.leasilycrm.propertyOwners

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyOwnerRepository
import com.fstengineering.leasilycrm.core.domain.repositories.UserRepository
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.domain.validators.interfaces.PropertyOwnerValidator
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toUiMessage
import com.fstengineering.leasilycrm.customerSegments.CustomerSegmentsViewModel
import com.fstengineering.leasilycrm.customerSegments.models.CustomerSegmentFormUiState
import com.fstengineering.leasilycrm.customerSegments.models.CustomerSegmentTypeUiState
import com.fstengineering.leasilycrm.propertyOwners.models.PropertyOwnerUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PropertyOwnersViewModel(
    private val propertyOwnerRepository: PropertyOwnerRepository,
    private val userRepository: UserRepository,
    private val propertyOwnerValidator: PropertyOwnerValidator,
) : ViewModel() {
    private var isInitialized = false

    private val _uiEvents = Channel<UiEvents>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _propertyOwners =
        MutableStateFlow<ImmutableList<PropertyOwnerUiState>>(persistentListOf())
    val propertyOwners = _propertyOwners.asStateFlow()

    private val _propertyOwnerToDelete = MutableStateFlow<PropertyOwnerUiState?>(null)
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
                        val uiState = propertyOwners
                            .map { PropertyOwnerUiState.fromDomain(it) }
                            .toPersistentList()
                        _propertyOwners.update { uiState }
                    }

                _isLoading.update { false }
            }
        }
    }

    fun onDeletePropertyOwner(
        propertyOwner: PropertyOwnerUiState?,
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
        data class DataSourceError(@StringRes val messageId: Int) : UiEvents

        data object RedirectToSignIn : UiEvents

        data object PropertyOwnerDeleteSuccess : UiEvents
    }
}
