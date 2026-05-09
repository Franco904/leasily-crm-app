package com.fstengineering.leasilycrm.propertyDashboard.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.application.repositories.PropertySharedCommandRepository
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.QueryFlowUseCase
import com.fstengineering.leasilycrm.propertyDashboard.application.models.dtos.PropertyReadModel
import com.fstengineering.leasilycrm.propertyDashboard.application.models.queries.PropertyArgs
import com.fstengineering.leasilycrm.propertyDashboard.presentation.mappers.toUiState
import com.fstengineering.leasilycrm.propertyDashboard.presentation.models.PropertyUiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PropertyDashboardViewModel(
    private val watchProperty: QueryFlowUseCase<PropertyArgs, PropertyReadModel>,
    private val propertySharedCommandRepository: PropertySharedCommandRepository,
) : ViewModel() {
    private var isInitialized = false

    private val _uiEvents = Channel<UiEvent>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _property = MutableStateFlow<PropertyUiState>(PropertyUiState.Loading)
    val property = _property.asStateFlow()

    fun init(propertyId: Long) {
        if (isInitialized) return

        watchProperty(propertyId = propertyId)

        isInitialized = true
    }

    private fun watchProperty(propertyId: Long) {
        viewModelScope.launch {
            watchProperty.invoke(PropertyArgs(propertyId)).collect { result ->
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

        viewModelScope.launch {
            propertySharedCommandRepository.delete(id = propertyId)
                .onError { e -> _uiEvents.trySend(UiEvent.BottomSheetError(e)) }
                .onSuccess { _uiEvents.trySend(UiEvent.PropertyDeleteSuccess) }
        }
    }

    sealed interface UiEvent {
        data class BottomSheetError(val error: ApplicationError) : UiEvent
        data object NavigateUp : UiEvent
        data object PropertyDeleteSuccess : UiEvent
    }
}
