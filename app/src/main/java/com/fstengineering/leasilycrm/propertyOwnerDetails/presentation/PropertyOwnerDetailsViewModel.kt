package com.fstengineering.leasilycrm.propertyOwnerDetails.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.application.repositories.PropertyOwnerSharedCommandRepository
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.propertyOwnerDetails.application.repositories.PropertyOwnerQueryRepository
import com.fstengineering.leasilycrm.propertyOwnerDetails.presentation.mappers.toUiState
import com.fstengineering.leasilycrm.propertyOwnerDetails.presentation.models.PropertyOwnerUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PropertyOwnerDetailsViewModel(
    private val propertyOwnerQueryRepository: PropertyOwnerQueryRepository,
    private val propertyOwnerSharedCommandRepository: PropertyOwnerSharedCommandRepository,
) : ViewModel() {
    private var isInitialized = false

    private val _uiEvents = Channel<UiEvent>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _propertyOwner =
        MutableStateFlow<PropertyOwnerUiState>(PropertyOwnerUiState.Loading)
    val propertyOwner = _propertyOwner.asStateFlow()

    private var watchPropertyOwnerJob: Job? = null

    fun init(propertyOwnerId: Long) {
        if (isInitialized) return

        watchPropertyOwner(propertyOwnerId = propertyOwnerId)

        isInitialized = true
    }

    private fun watchPropertyOwner(propertyOwnerId: Long) {
        watchPropertyOwnerJob = viewModelScope.launch {
            propertyOwnerQueryRepository.watchById(propertyOwnerId).collect { result ->
                result
                    .onError { e -> _uiEvents.send(UiEvent.BottomSheetError(e)) }
                    .onSuccess { propertyOwner ->
                        _propertyOwner.update { propertyOwner.toUiState() }
                    }
            }
        }
    }

    fun deletePropertyOwner(propertyOwnerId: Long) {
        watchPropertyOwnerJob?.cancel()

        viewModelScope.launch {
            propertyOwnerSharedCommandRepository.delete(id = propertyOwnerId)
                .onError { e -> _uiEvents.send(UiEvent.BottomSheetError(e)) }
                .onSuccess { _uiEvents.send(UiEvent.PropertyOwnerDeleteSuccess) }
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
