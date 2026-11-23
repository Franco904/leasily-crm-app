package com.fstengineering.leasilycrm.clienteDetails.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.Destinations
import com.fstengineering.leasilycrm.clienteDetails.infra.dataSources.ClienteDetailsReadDataSource
import com.fstengineering.leasilycrm.clienteDetails.presentation.mappers.toClienteDetailsUiState
import com.fstengineering.leasilycrm.clienteDetails.presentation.models.ClienteDetailsUiState
import com.fstengineering.leasilycrm.clienteRegistration.infra.dataSources.ClienteWriteDataSource
import com.fstengineering.leasilycrm.core.domain.utils.BaseError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.infra.dataSources.database.AtendimentoSharedWriteDataSource
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ClienteDetailsViewModel(
    private val route: Destinations.ClienteDetails,
    private val clienteDetailsReadDataSource: ClienteDetailsReadDataSource,
    private val clienteWriteDataSource: ClienteWriteDataSource,
    private val atendimentoSharedWriteDataSource: AtendimentoSharedWriteDataSource,
) : ViewModel() {
    private var isLoaded = false

    private val _uiEvents = Channel<UiEvent>(Channel.BUFFERED)
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _uiState = MutableStateFlow<ClienteDetailsUiState>(ClienteDetailsUiState.Loading)
    val uiState: StateFlow<ClienteDetailsUiState> = _uiState.asStateFlow()

    private var watchJob: Job? = null

    fun init() {
        if (isLoaded) return
        isLoaded = true

        watchCliente()
    }

    private fun watchCliente() {
        watchJob = viewModelScope.launch {
            clienteDetailsReadDataSource.watchCliente(route.clienteId).collect { result ->
                result
                    .onError { e -> _uiEvents.trySend(UiEvent.ShowErrorBottomSheet(e)) }
                    .onSuccess { readModel ->
                        if (readModel == null) {
                            _uiEvents.trySend(UiEvent.NavigateUp)
                            watchJob?.cancel()
                            return@onSuccess
                        }

                        _uiState.update { readModel.toClienteDetailsUiState() }
                    }
            }
        }
    }

    fun onDeleteCliente() {
        val currentState = _uiState.value as? ClienteDetailsUiState.Loaded ?: return

        watchJob?.cancel()

        viewModelScope.launch {
            clienteWriteDataSource.delete(currentState.id)
                .onError { error ->
                    _uiEvents.trySend(UiEvent.ShowErrorBottomSheet(error))
                    watchCliente()

                    return@launch
                }

            _uiEvents.trySend(UiEvent.ShowClienteDeleteSnackbar(isLead = currentState.isLead))
        }
    }

    fun deleteCurrentAtendimento(atendimentoId: Long) {
        viewModelScope.launch {
            atendimentoSharedWriteDataSource.delete(atendimentoId)
                .onError { error ->
                    _uiEvents.trySend(UiEvent.ShowErrorBottomSheet(error))

                    return@launch
                }

            _uiEvents.trySend(UiEvent.ShowAtendimentoDeleteSnackbar)
        }
    }

    override fun onCleared() {
        _uiEvents.close()

        super.onCleared()
    }

    sealed interface UiEvent {
        data class ShowErrorBottomSheet(val error: BaseError) : UiEvent
        data object NavigateUp : UiEvent
        data class ShowClienteDeleteSnackbar(val isLead: Boolean) : UiEvent
        data object ShowAtendimentoDeleteSnackbar : UiEvent
    }
}
