package com.fstengineering.leasilycrm.occupiedPeriodDetails.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.application.models.commands.RegisterCheckOutCommand
import com.fstengineering.leasilycrm.core.application.models.commands.UndoCheckOutCommand
import com.fstengineering.leasilycrm.core.application.repositories.OccupiedPeriodSharedCommandRepository
import com.fstengineering.leasilycrm.core.application.useCases.RegisterStayCheckOutUseCase
import com.fstengineering.leasilycrm.core.application.useCases.UndoRegisterStayCheckOutUseCase
import com.fstengineering.leasilycrm.core.domain.utils.BaseError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.occupiedPeriodDetails.application.models.queries.OccupiedPeriodArgs
import com.fstengineering.leasilycrm.occupiedPeriodDetails.application.repositories.OccupiedPeriodQueryRepository
import com.fstengineering.leasilycrm.occupiedPeriodDetails.application.useCases.WatchOccupiedPeriodUseCase
import com.fstengineering.leasilycrm.occupiedPeriodDetails.presentation.mappers.toUiState
import com.fstengineering.leasilycrm.occupiedPeriodDetails.presentation.models.OccupiedPeriodDetailsUiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OccupiedPeriodDetailsViewModel(
    private val watchOccupiedPeriod: WatchOccupiedPeriodUseCase,
    private val registerStayCheckOut: RegisterStayCheckOutUseCase,
    private val undoRegisterStayCheckOut: UndoRegisterStayCheckOutUseCase,
    private val occupiedPeriodSharedCommandRepository: OccupiedPeriodSharedCommandRepository,
    private val occupiedPeriodQueryRepository: OccupiedPeriodQueryRepository,
) : ViewModel() {
    private var isInitialized = false

    private val _uiEvents = Channel<UiEvent>(Channel.BUFFERED)
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _uiState =
        MutableStateFlow<OccupiedPeriodDetailsUiState>(OccupiedPeriodDetailsUiState.Loading)
    val uiState = _uiState.asStateFlow()

    var mustNotShowCheckOutDialog: Boolean = false
        private set

    fun init(stayId: Long) {
        if (isInitialized) return

        watchOccupiedPeriod(stayId)
        watchCheckOutDialogPreference()

        isInitialized = true
    }

    private fun watchOccupiedPeriod(stayId: Long) {
        viewModelScope.launch {
            watchOccupiedPeriod.invoke(OccupiedPeriodArgs(stayId)).collect { result ->
                result
                    .onError { e -> _uiEvents.trySend(UiEvent.ShowError(e)) }
                    .onSuccess { storedPeriod ->
                        if (storedPeriod == null) {
                            _uiEvents.trySend(UiEvent.NavigateUp)
                            return@onSuccess
                        }

                        _uiState.update { storedPeriod.toUiState() }
                    }
            }
        }
    }

    private fun watchCheckOutDialogPreference() {
        viewModelScope.launch {
            occupiedPeriodQueryRepository.watchMustNotShowCheckOutDialogAnymore()
                .collect { result ->
                    result.onSuccess { mustNotShowCheckOutDialog = it }
                }
        }
    }

    fun deletePeriod() {
        val periodId = (_uiState.value as? OccupiedPeriodDetailsUiState.Loaded)?.id ?: return

        viewModelScope.launch {
            occupiedPeriodSharedCommandRepository.delete(periodId)
                .onError { e -> _uiEvents.trySend(UiEvent.ShowError(e)) }
                .onSuccess { _uiEvents.trySend(UiEvent.FinishOccupiedPeriodDelete) }
        }
    }

    fun registerStayCheckIn() {
        val periodId = (_uiState.value as? OccupiedPeriodDetailsUiState.Loaded)?.id ?: return

        viewModelScope.launch {
            occupiedPeriodSharedCommandRepository.registerCheckIn(periodId)
                .onError { e -> _uiEvents.trySend(UiEvent.ShowError(e)) }
                .onSuccess { _uiEvents.trySend(UiEvent.RegisterCheckInSuccess) }
        }
    }

    fun undoRegisterStayCheckIn() {
        val periodId = (_uiState.value as? OccupiedPeriodDetailsUiState.Loaded)?.id ?: return

        viewModelScope.launch {
            occupiedPeriodSharedCommandRepository.undoRegisterCheckIn(periodId)
                .onError { e -> _uiEvents.trySend(UiEvent.ShowError(e)) }
        }
    }

    fun registerStayCheckOut(mustNotShowCheckOutDialogAnymore: Boolean = true) {
        val loaded = _uiState.value as? OccupiedPeriodDetailsUiState.Loaded ?: return

        viewModelScope.launch {
            registerStayCheckOut(
                RegisterCheckOutCommand(
                    stayId = loaded.id,
                    tenantId = loaded.tenantId
                )
            )
                .onError { error ->
                    _uiEvents.trySend(UiEvent.ShowError(error))
                }
                .onSuccess { _uiEvents.trySend(UiEvent.RegisterCheckOutSuccess) }
        }

        viewModelScope.launch {
            occupiedPeriodSharedCommandRepository.setMustNotShowCheckOutDialogAnymore(
                mustNotShowCheckOutDialogAnymore,
            )
                .onError { e -> _uiEvents.trySend(UiEvent.ShowError(e)) }
        }
    }

    fun undoRegisterStayCheckOut() {
        val loaded = _uiState.value as? OccupiedPeriodDetailsUiState.Loaded ?: return

        viewModelScope.launch {
            undoRegisterStayCheckOut(
                UndoCheckOutCommand(
                    stayId = loaded.id,
                    tenantId = loaded.tenantId
                )
            )
                .onError { error ->
                    _uiEvents.trySend(UiEvent.ShowError(error))
                }
        }
    }

    override fun onCleared() {
        _uiEvents.close()

        super.onCleared()
    }

    sealed interface UiEvent {
        data class ShowError(val error: BaseError) : UiEvent
        data object NavigateUp : UiEvent
        data object FinishOccupiedPeriodDelete : UiEvent
        data object RegisterCheckInSuccess : UiEvent
        data object RegisterCheckOutSuccess : UiEvent
    }
}
