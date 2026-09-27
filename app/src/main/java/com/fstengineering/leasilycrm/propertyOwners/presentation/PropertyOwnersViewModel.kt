package com.fstengineering.leasilycrm.propertyOwners.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.application.repositories.PropertyOwnerSharedCommandRepository
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.presentation.models.PropertyOwnerUiState
import com.fstengineering.leasilycrm.propertyOwners.application.repositories.PropertyOwnerQueryRepository
import com.fstengineering.leasilycrm.propertyOwners.presentation.mappers.toUiState
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
    private val propertyOwnerQueryRepository: PropertyOwnerQueryRepository,
    private val propertyOwnerSharedCommandRepository: PropertyOwnerSharedCommandRepository,
) : ViewModel() {
    private var isInitialized = false

    private val _uiEvents = Channel<UiEvent>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _propertyOwners =
        MutableStateFlow<ImmutableList<PropertyOwnerUiState>>(persistentListOf())
    val propertyOwners = _propertyOwners.asStateFlow()

    private val _propertyOwnerToDelete = MutableStateFlow<PropertyOwnerUiState?>(null)
    val propertyOwnerToDelete = _propertyOwnerToDelete.asStateFlow()

    fun init() {
        if (isInitialized) return

        watchPropertyOwners()

        isInitialized = true
    }

    private fun watchPropertyOwners() {
        viewModelScope.launch {
            propertyOwnerQueryRepository.watchAll().collect { result ->
                result
                    .onError { e -> _uiEvents.send(UiEvent.BottomSheetError(e)) }
                    .onSuccess { propertyOwners ->
                        val states = propertyOwners.map { it.toUiState() }.toPersistentList()
                        _propertyOwners.update { states }
                    }
            }
        }
    }

    fun onDeletePropertyOwner(propertyOwner: PropertyOwnerUiState?) {
        _propertyOwnerToDelete.update { propertyOwner }
    }

    fun deletePropertyOwner() {
        viewModelScope.launch {
            val id = propertyOwnerToDelete.value?.id ?: return@launch

            propertyOwnerSharedCommandRepository.delete(id)
                .onError { e -> _uiEvents.send(UiEvent.BottomSheetError(e)) }
                .onSuccess {
                    _uiEvents.send(UiEvent.PropertyOwnerDeleteSuccess)

                    onDeletePropertyOwner(propertyOwner = null)
                }
        }
    }

    override fun onCleared() {
        _uiEvents.close()

        super.onCleared()
    }

    sealed interface UiEvent {
        data class BottomSheetError(val error: ApplicationError) : UiEvent
        data object PropertyOwnerDeleteSuccess : UiEvent
    }
}
