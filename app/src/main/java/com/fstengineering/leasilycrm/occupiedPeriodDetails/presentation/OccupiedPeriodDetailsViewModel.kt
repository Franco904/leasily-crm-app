package com.fstengineering.leasilycrm.occupiedPeriodDetails.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.application.repositories.OccupiedPeriodSharedCommandRepository
import com.fstengineering.leasilycrm.core.application.useCases.RegisterStayCheckOutUseCase
import com.fstengineering.leasilycrm.core.application.useCases.UndoRegisterStayCheckOutUseCase
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
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
                    .onError { e -> _uiEvents.trySend(UiEvents.BottomSheetError(e)) }
                    .onSuccess { storedPeriod ->
                        if (storedPeriod == null) {
                            _uiEvents.trySend(UiEvents.NavigateUp)
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
                .onError { e -> _uiEvents.trySend(UiEvents.BottomSheetError(e)) }
                .onSuccess { _uiEvents.trySend(UiEvents.OccupiedPeriodDeleteSuccess) }
        }
    }

    fun registerStayCheckIn() {
        val periodId = (_uiState.value as? OccupiedPeriodDetailsUiState.Loaded)?.id ?: return

        viewModelScope.launch {
            occupiedPeriodSharedCommandRepository.registerCheckIn(periodId)
                .onError { e -> _uiEvents.trySend(UiEvents.BottomSheetError(e)) }
                .onSuccess { _uiEvents.trySend(UiEvents.RegisterCheckInSuccess) }
        }
    }

    fun undoRegisterStayCheckIn() {
        val periodId = (_uiState.value as? OccupiedPeriodDetailsUiState.Loaded)?.id ?: return

        viewModelScope.launch {
            occupiedPeriodSharedCommandRepository.undoRegisterCheckIn(periodId)
                .onError { e -> _uiEvents.trySend(UiEvents.BottomSheetError(e)) }
        }
    }

    fun registerStayCheckOut(mustNotShowCheckOutDialogAnymore: Boolean = true) {
        val loaded = _uiState.value as? OccupiedPeriodDetailsUiState.Loaded ?: return

        viewModelScope.launch {
            registerStayCheckOut(
                stayId = loaded.id,
                tenantId = loaded.tenantId,
            )
                .onError { e -> _uiEvents.trySend(UiEvents.BottomSheetError(e)) }
                .onSuccess { _uiEvents.trySend(UiEvents.RegisterCheckOutSuccess) }
        }

        viewModelScope.launch {
            occupiedPeriodSharedCommandRepository.setMustNotShowCheckOutDialogAnymore(
                mustNotShowCheckOutDialogAnymore,
            )
                .onError { e -> _uiEvents.trySend(UiEvents.BottomSheetError(e)) }
        }
    }

    fun undoRegisterStayCheckOut() {
        val loaded = _uiState.value as? OccupiedPeriodDetailsUiState.Loaded ?: return

        viewModelScope.launch {
            undoRegisterStayCheckOut(
                stayId = loaded.id,
                tenantId = loaded.tenantId,
            )
                .onError { e -> _uiEvents.trySend(UiEvents.BottomSheetError(e)) }
        }
    }

    override fun onCleared() {
        _uiEvents.close()

        super.onCleared()
    }

    sealed interface UiEvents {
        data class BottomSheetError(val error: ApplicationError) : UiEvents
        data object NavigateUp : UiEvents
        data object OccupiedPeriodDeleteSuccess : UiEvents
        data object RegisterCheckInSuccess : UiEvents
        data object RegisterCheckOutSuccess : UiEvents
    }
}
