package com.fstengineering.leasilycrm.proprietarioDetails.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.Destinations
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.proprietarioDetails.infra.dataSources.ProprietarioDetailsReadDataSource
import com.fstengineering.leasilycrm.proprietarioDetails.presentation.mappers.toProprietarioUiState
import com.fstengineering.leasilycrm.proprietarioDetails.presentation.models.ProprietarioUiState
import com.fstengineering.leasilycrm.proprietarioRegistration.infra.dataSources.ProprietarioWriteDataSource
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProprietarioDetailsViewModel(
    route: Destinations.ProprietarioDetails,
    private val proprietarioDetailsReadDataSource: ProprietarioDetailsReadDataSource,
    private val proprietarioWriteDataSource: ProprietarioWriteDataSource,
) : ViewModel() {
    private val proprietarioId = route.proprietarioId

    private val _uiEvents = Channel<UiEvent>(Channel.BUFFERED)
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _uiState =
        MutableStateFlow<ProprietarioUiState>(ProprietarioUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private var watchProprietarioJob: Job? = null
    private var isLoaded = false

    fun init() {
        if (isLoaded) return
        isLoaded = true

        watchProprietarioJob = viewModelScope.launch {
            proprietarioDetailsReadDataSource.watchProprietario(proprietarioId).collect { result ->
                result
                    .onError { e -> _uiEvents.trySend(UiEvent.ShowErrorBottomSheet(e)) }
                    .onSuccess { readModel ->
                        readModel ?: return@onSuccess
                        _uiState.update { readModel.toProprietarioUiState() }
                    }
            }
        }
    }

    fun deleteProprietario() {
        watchProprietarioJob?.cancel()

        viewModelScope.launch {
            proprietarioWriteDataSource.delete(proprietarioId = proprietarioId)
                .onError { e -> _uiEvents.trySend(UiEvent.ShowErrorBottomSheet(e)) }
                .onSuccess { _uiEvents.trySend(UiEvent.ShowDeleteSuccessSnackbar) }
        }
    }

    override fun onCleared() {
        _uiEvents.close()

        super.onCleared()
    }

    sealed interface UiEvent {
        data class ShowErrorBottomSheet(val error: ApplicationError) : UiEvent
        data object ShowDeleteSuccessSnackbar : UiEvent
    }
}
