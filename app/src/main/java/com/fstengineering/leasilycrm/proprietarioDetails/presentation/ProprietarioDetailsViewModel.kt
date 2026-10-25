package com.fstengineering.leasilycrm.proprietarioDetails.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.application.repositories.ProprietarioSharedCommandRepository
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.proprietarioDetails.application.repositories.ProprietarioQueryRepository
import com.fstengineering.leasilycrm.proprietarioDetails.presentation.mappers.toUiState
import com.fstengineering.leasilycrm.proprietarioDetails.presentation.models.ProprietarioUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProprietarioDetailsViewModel(
    private val proprietarioQueryRepository: ProprietarioQueryRepository,
    private val proprietarioSharedCommandRepository: ProprietarioSharedCommandRepository,
) : ViewModel() {
    private var isInitialized = false

    private val _uiEvents = Channel<UiEvent>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _proprietario =
        MutableStateFlow<ProprietarioUiState>(ProprietarioUiState.Loading)
    val proprietario = _proprietario.asStateFlow()

    private var watchProprietarioJob: Job? = null

    fun init(proprietarioId: Long) {
        if (isInitialized) return

        watchProprietario(proprietarioId = proprietarioId)

        isInitialized = true
    }

    private fun watchProprietario(proprietarioId: Long) {
        watchProprietarioJob = viewModelScope.launch {
            proprietarioQueryRepository.watchById(proprietarioId).collect { result ->
                result
                    .onError { e -> _uiEvents.send(UiEvent.BottomSheetError(e)) }
                    .onSuccess { proprietario ->
                        _proprietario.update { proprietario.toUiState() }
                    }
            }
        }
    }

    fun deleteProprietario(proprietarioId: Long) {
        watchProprietarioJob?.cancel()

        viewModelScope.launch {
            proprietarioSharedCommandRepository.delete(id = proprietarioId)
                .onError { e -> _uiEvents.send(UiEvent.BottomSheetError(e)) }
                .onSuccess { _uiEvents.send(UiEvent.ProprietarioDeleteSuccess) }
        }
    }

    override fun onCleared() {
        _uiEvents.close()

        super.onCleared()
    }

    sealed interface UiEvent {
        data class BottomSheetError(val error: ApplicationError) : UiEvent
        data object ProprietarioDeleteSuccess : UiEvent
    }
}
