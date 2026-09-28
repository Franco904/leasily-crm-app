package com.fstengineering.leasilycrm.leadTenantDetails.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.ValidationErrors
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.leadTenantDetails.application.models.commands.DeleteAssistanceCommand
import com.fstengineering.leasilycrm.leadTenantDetails.application.models.commands.DeleteLeadTenantCommand
import com.fstengineering.leasilycrm.leadTenantDetails.application.models.queries.LeadTenantArgs
import com.fstengineering.leasilycrm.leadTenantDetails.application.useCases.DeleteAssistanceUseCase
import com.fstengineering.leasilycrm.leadTenantDetails.application.useCases.DeleteLeadTenantUseCase
import com.fstengineering.leasilycrm.leadTenantDetails.application.useCases.WatchLeadTenantUseCase
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
    private val watchLeadTenant: WatchLeadTenantUseCase,
    private val deleteLeadTenant: DeleteLeadTenantUseCase,
    private val deleteAssistance: DeleteAssistanceUseCase,
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
                .onError { error ->
                    when (error) {
                        is ApplicationError -> _uiEvents.trySend(UiEvents.BottomSheetError(error))
                        is ValidationErrors, is DomainError -> Unit
                    }

                    return@launch
                }

            _uiEvents.trySend(UiEvents.LeadTenantDeleteSuccess(isLead = currentState.isLead))
        }
    }

    fun deleteCurrentAssistance(assistanceId: Long) {
        viewModelScope.launch {
            deleteAssistance(DeleteAssistanceCommand(assistanceId))
                .onError { error ->
                    when (error) {
                        is ApplicationError -> _uiEvents.trySend(UiEvents.BottomSheetError(error))
                        is ValidationErrors, is DomainError -> Unit
                    }

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
