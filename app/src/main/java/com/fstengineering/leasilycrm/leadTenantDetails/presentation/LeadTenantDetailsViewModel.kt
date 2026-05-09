package com.fstengineering.leasilycrm.leadTenantDetails.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.CommandUseCase
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.QueryFlowUseCase
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.presentation.utils.onApplicationError
import com.fstengineering.leasilycrm.leadTenantDetails.application.models.commands.DeleteAssistanceCommand
import com.fstengineering.leasilycrm.leadTenantDetails.application.models.commands.DeleteLeadTenantCommand
import com.fstengineering.leasilycrm.leadTenantDetails.application.models.dtos.LeadTenantReadModel
import com.fstengineering.leasilycrm.leadTenantDetails.application.models.queries.LeadTenantArgs
import com.fstengineering.leasilycrm.leadTenantDetails.presentation.mappers.toUiState
import com.fstengineering.leasilycrm.leadTenantDetails.presentation.models.LeadTenantDetailsUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LeadTenantDetailsViewModel(
    private val watchLeadTenant: QueryFlowUseCase<LeadTenantArgs, LeadTenantReadModel?>,
    private val deleteLeadTenant: CommandUseCase<DeleteLeadTenantCommand>,
    private val deleteAssistance: CommandUseCase<DeleteAssistanceCommand>,
) : ViewModel() {
    private var isInitialized = false

    private val _uiEvents = Channel<UiEvents>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _uiState =
        MutableStateFlow<LeadTenantDetailsUiState>(LeadTenantDetailsUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private var watchJob: Job? = null

    fun init(leadTenantId: Long) {
        if (isInitialized) return

        watchJob = viewModelScope.launch {
            watchLeadTenant(LeadTenantArgs(leadTenantId)).collect { result ->
                result
                    .onError { e -> _uiEvents.trySend(UiEvents.BottomSheetError(e)) }
                    .onSuccess { readModel ->
                        if (readModel == null) {
                            _uiEvents.trySend(UiEvents.NavigateUp)
                            watchJob?.cancel()
                            return@onSuccess
                        }

                        _uiState.update { readModel.toUiState() }
                    }
            }
        }

        isInitialized = true
    }

    fun onDeleteLeadTenant() {
        val currentState = _uiState.value as? LeadTenantDetailsUiState.Loaded ?: return

        watchJob?.cancel()

        viewModelScope.launch {
            deleteLeadTenant(DeleteLeadTenantCommand(currentState.id))
                .onApplicationError { e ->
                    _uiEvents.trySend(UiEvents.BottomSheetError(e))
                    return@launch
                }

            _uiEvents.trySend(UiEvents.LeadTenantDeleteSuccess(isLead = currentState.isLead))
        }
    }

    fun deleteCurrentAssistance(assistanceId: Long) {
        viewModelScope.launch {
            deleteAssistance(DeleteAssistanceCommand(assistanceId))
                .onApplicationError { e ->
                    _uiEvents.trySend(UiEvents.BottomSheetError(e))
                    return@launch
                }

            _uiEvents.trySend(UiEvents.CurrentAssistanceDeleteSuccess)
        }
    }

    override fun onCleared() {
        _uiEvents.close()

        super.onCleared()
    }

    sealed interface UiEvents {
        data class BottomSheetError(val error: ApplicationError) : UiEvents
        data object NavigateUp : UiEvents
        data class LeadTenantDeleteSuccess(val isLead: Boolean) : UiEvents
        data object CurrentAssistanceDeleteSuccess : UiEvents
    }
}
