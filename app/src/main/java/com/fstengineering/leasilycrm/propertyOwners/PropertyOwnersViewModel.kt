package com.fstengineering.leasilycrm.propertyOwners

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.application.repositories.PropertyOwnerSharedCommandRepository
import com.fstengineering.leasilycrm.core.domain.entities.PropertyOwner
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.infra.utils.InfraError
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PropertyOwnersViewModel(
    private val propertyOwnerSharedCommandRepository: PropertyOwnerSharedCommandRepository,
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

        watchPropertyOwners()

        isInitialized = true
    }

    private fun watchPropertyOwners() {
        _isLoading.update { true }

        viewModelScope.launch {
            propertyOwnerSharedCommandRepository.watchAll().collect { result ->
                result
                    .onError { e -> _uiEvents.send(UiEvents.Error(e)) }
                    .onSuccess { propertyOwners -> _propertyOwners.update { propertyOwners } }

                _isLoading.update { false }
            }
        }
    }

    fun onDeletePropertyOwner(propertyOwner: PropertyOwner?) {
        _propertyOwnerToDelete.update { propertyOwner }
    }

    fun deletePropertyOwner() {
        viewModelScope.launch {
            val id = propertyOwnerToDelete.value?.id ?: return@launch

            propertyOwnerSharedCommandRepository.delete(id)
                .onError { e -> _uiEvents.send(UiEvents.Error(e)) }
                .onSuccess {
                    _uiEvents.send(UiEvents.PropertyOwnerDeleteSuccess)

                    onDeletePropertyOwner(propertyOwner = null)
                }
        }
    }

    sealed interface UiEvents {
        data class Error(val error: InfraError) : UiEvents
        data object PropertyOwnerDeleteSuccess : UiEvents
    }
}
