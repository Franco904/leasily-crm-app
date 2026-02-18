package com.fstengineering.leasilycrm.stayDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.application.repositories.StayRepository
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.GetStaysStructuredDatesUseCase
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.RegisterStayCheckOutUseCase
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.StaysStructuredDatesMap
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.UndoRegisterStayCheckOutUseCase
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.stayDetails.model.StayDetailsUiState
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StayDetailsViewModel(
    private val registerStayCheckOutUseCase: RegisterStayCheckOutUseCase,
    private val undoRegisterStayCheckOutUseCase: UndoRegisterStayCheckOutUseCase,
    private val getStaysStructuredDatesUseCase: GetStaysStructuredDatesUseCase,
    private val stayRepository: StayRepository,
) : ViewModel() {
    private var isInitialized = false

    private val _uiEvents = Channel<UiEvents>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _stay = MutableStateFlow<StayDetailsUiState>(StayDetailsUiState.Empty)
    val stay = _stay.asStateFlow()

    private val _stayStructuredDates = MutableStateFlow<StaysStructuredDatesMap>(persistentMapOf())
    val stayStructuredDates = _stayStructuredDates.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    var mustNotShowCheckOutDialog: Boolean = true
        private set

    fun init(stayId: Long) {
        if (isInitialized) return

        watchStay(stayId = stayId)

        isInitialized = true
    }

    private fun watchStay(stayId: Long) {
        _isLoading.update { true }

        viewModelScope.launch {
            stayRepository.watchById(stayId).collect { result ->
                result
                    .onError { e -> _uiEvents.send(UiEvents.Error(e)) }
                    .onSuccess { storedStay ->
                        if (storedStay == null) {
                            _uiEvents.send(UiEvents.NavigateUp)
                            return@onSuccess
                        }

                        _stay.update { storedStay }

                        _stayStructuredDates.update {
                            getStaysStructuredDatesUseCase(stays = persistentListOf(storedStay))
                        }
                    }

                _isLoading.update { false }
            }
        }
    }

    fun deleteStay() {
        viewModelScope.launch {
            stayRepository.delete(id = stay.value?.id!!)
                .onError { e -> _uiEvents.send(UiEvents.Error(e)) }
                .onSuccess { _uiEvents.send(UiEvents.StayDeleteSuccess) }
        }
    }

    fun registerStayCheckIn() {
        viewModelScope.launch {
            val updatedStay = stay.value?.copy(isCheckInDone = true)

            stayRepository.insertOrUpdate(stay = updatedStay)
                .onError { e -> _uiEvents.send(UiEvents.Error(e)) }
                .onSuccess { _uiEvents.send(UiEvents.RegisterCheckInSuccess) }
        }
    }

    fun undoRegisterStayCheckIn() {
        viewModelScope.launch {
            val updatedStay = stay.value.copy(isCheckInDone = false)

            stayRepository.insertOrUpdate(stay = updatedStay)
                .onError { e -> _uiEvents.send(UiEvents.Error(e)) }
        }
    }

    fun registerStayCheckOut(
        mustNotShowCheckOutDialogAnymore: Boolean = true,
    ) {
        viewModelScope.launch {
            registerStayCheckOutUseCase(stay = stay.value)
                .onError { e -> _uiEvents.send(UiEvents.Error(e)) }
                .onSuccess { _uiEvents.send(UiEvents.RegisterCheckOutSuccess) }
        }

        viewModelScope.launch {
            stayRepository.setMustNotShowCheckOutDialogAnymore(
                mustNotShowCheckOutDialogAnymore = mustNotShowCheckOutDialogAnymore,
            )
                .onError { e -> _uiEvents.send(UiEvents.Error(e)) }
        }
    }

    fun undoRegisterStayCheckOut() {
        viewModelScope.launch {
            undoRegisterStayCheckOutUseCase(stay = stay.value)
                .onError { e -> _uiEvents.send(UiEvents.Error(e)) }
        }
    }

    sealed interface UiEvents {
        data class Error(val error: DataError) : UiEvents
        data object NavigateUp : UiEvents
        data object StayDeleteSuccess : UiEvents
        data object RegisterCheckInSuccess : UiEvents
        data object RegisterCheckOutSuccess : UiEvents
    }
}
