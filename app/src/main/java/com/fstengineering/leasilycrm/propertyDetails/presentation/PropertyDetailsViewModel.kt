package com.fstengineering.leasilycrm.propertyDetails.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.application.repositories.PropertySharedCommandRepository
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.propertyDetails.application.repositories.PropertyQueryRepository
import com.fstengineering.leasilycrm.propertyDetails.presentation.mappers.toUiState
import com.fstengineering.leasilycrm.propertyDetails.presentation.models.PropertyUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PropertyDetailsViewModel(
    private val propertyQueryRepository: PropertyQueryRepository,
    private val propertySharedCommandRepository: PropertySharedCommandRepository,
) : ViewModel() {
    private var isInitialized = false

    private val _uiEvents = Channel<UiEvent>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _property = MutableStateFlow<PropertyUiState>(PropertyUiState.Loading)
    val property = _property.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private var watchPropertyJob: Job? = null

    fun init(propertyId: Long) {
        if (isInitialized) return

        watchProperty(propertyId = propertyId)

        isInitialized = true
    }

    private fun watchProperty(propertyId: Long) {
        _isLoading.update { true }

        watchPropertyJob = viewModelScope.launch {
            propertyQueryRepository.watchById(propertyId)
                .collectLatest { result ->
                    result
                        .onError { e -> _uiEvents.trySend(UiEvent.BottomSheetError(e)) }
                        .onSuccess { storedProperty ->
                            _property.update { storedProperty.toUiState() }
                        }
                }
        }
    }

    fun deleteProperty() {
        val propertyId = (property.value as? PropertyUiState.Loaded)?.id ?: return

        watchPropertyJob?.cancel()

        viewModelScope.launch {
            propertySharedCommandRepository.delete(id = propertyId)
                .onError { e -> _uiEvents.trySend(UiEvent.BottomSheetError(e)) }
                .onSuccess { _uiEvents.trySend(UiEvent.PropertyDeleteSuccess) }
        }
    }

    override fun onCleared() {
        _uiEvents.close()

        super.onCleared()
    }

    sealed interface UiEvent {
        data class BottomSheetError(val error: ApplicationError) : UiEvent

        data object NavigateUp : UiEvent

        data object PropertyDeleteSuccess : UiEvent
    }
}
