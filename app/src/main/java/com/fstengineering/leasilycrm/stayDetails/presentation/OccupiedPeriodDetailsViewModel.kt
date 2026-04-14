package com.fstengineering.leasilycrm.stayDetails.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.stayDetails.application.repositories.OccupiedPeriodCommandRepository
import com.fstengineering.leasilycrm.stayDetails.application.repositories.OccupiedPeriodQueryRepository
import com.fstengineering.leasilycrm.stayDetails.application.useCases.RegisterStayCheckOutUseCase
import com.fstengineering.leasilycrm.stayDetails.application.useCases.UndoRegisterStayCheckOutUseCase
import com.fstengineering.leasilycrm.stayDetails.application.useCases.WatchOccupiedPeriodUseCase
import com.fstengineering.leasilycrm.stayDetails.presentation.mappers.toUiState
import com.fstengineering.leasilycrm.stayDetails.presentation.models.OccupiedPeriodDetailsUiState
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
    private val occupiedPeriodCommandRepository: OccupiedPeriodCommandRepository,
    private val occupiedPeriodQueryRepository: OccupiedPeriodQueryRepository,
) : ViewModel() {
    private var isInitialized = false

    private val _uiEvents = Channel<UiEvents>()
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
            watchOccupiedPeriod.invoke(stayId).collect { result ->
                result
                    .onError { e -> _uiEvents.send(UiEvents.BottomSheetError(e)) }
                    .onSuccess { storedPeriod ->
                        if (storedPeriod == null) {
                            _uiEvents.send(UiEvents.NavigateUp)
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
            occupiedPeriodCommandRepository.delete(periodId)
                .onError { e -> _uiEvents.send(UiEvents.BottomSheetError(e)) }
                .onSuccess { _uiEvents.send(UiEvents.StayDeleteSuccess) }
        }
    }

    fun registerStayCheckIn() {
        val periodId = (_uiState.value as? OccupiedPeriodDetailsUiState.Loaded)?.id ?: return

        viewModelScope.launch {
            occupiedPeriodCommandRepository.registerCheckIn(periodId)
                .onError { e -> _uiEvents.send(UiEvents.BottomSheetError(e)) }
                .onSuccess { _uiEvents.send(UiEvents.RegisterCheckInSuccess) }
        }
    }

    fun undoRegisterStayCheckIn() {
        val periodId = (_uiState.value as? OccupiedPeriodDetailsUiState.Loaded)?.id ?: return

        viewModelScope.launch {
            occupiedPeriodCommandRepository.undoRegisterCheckIn(periodId)
                .onError { e -> _uiEvents.send(UiEvents.BottomSheetError(e)) }
        }
    }

    fun registerStayCheckOut(mustNotShowCheckOutDialogAnymore: Boolean = true) {
        val loaded = _uiState.value as? OccupiedPeriodDetailsUiState.Loaded ?: return

        viewModelScope.launch {
            registerStayCheckOut(
                stayId = loaded.id,
                tenantId = loaded.tenantId,
            )
                .onError { e -> _uiEvents.send(UiEvents.BottomSheetError(e)) }
                .onSuccess { _uiEvents.send(UiEvents.RegisterCheckOutSuccess) }
        }

        viewModelScope.launch {
            occupiedPeriodCommandRepository.setMustNotShowCheckOutDialogAnymore(
                mustNotShowCheckOutDialogAnymore,
            )
                .onError { e -> _uiEvents.send(UiEvents.BottomSheetError(e)) }
        }
    }

    fun undoRegisterStayCheckOut() {
        val loaded = _uiState.value as? OccupiedPeriodDetailsUiState.Loaded ?: return

        viewModelScope.launch {
            undoRegisterStayCheckOut(
                stayId = loaded.id,
                tenantId = loaded.tenantId,
            )
                .onError { e -> _uiEvents.send(UiEvents.BottomSheetError(e)) }
        }
    }

    sealed interface UiEvents {
        data class BottomSheetError(val error: ApplicationError) : UiEvents
        data object NavigateUp : UiEvents
        data object StayDeleteSuccess : UiEvents
        data object RegisterCheckInSuccess : UiEvents
        data object RegisterCheckOutSuccess : UiEvents
    }
}
