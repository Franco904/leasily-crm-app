package com.fstengineering.leasilycrm.imovelDetails.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.Destinations
import com.fstengineering.leasilycrm.core.domain.utils.BaseError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.infra.dataSources.database.ImovelSharedWriteDataSource
import com.fstengineering.leasilycrm.imovelDetails.infra.dataSources.ImovelDetailsReadDataSource
import com.fstengineering.leasilycrm.imovelDetails.presentation.mappers.toUiState
import com.fstengineering.leasilycrm.imovelDetails.presentation.models.ImovelUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ImovelDetailsViewModel(
    private val route: Destinations.ImovelDetails,
    private val imovelDetailsReadDataSource: ImovelDetailsReadDataSource,
    private val imovelSharedWriteDataSource: ImovelSharedWriteDataSource,
) : ViewModel() {
    private var isLoaded = false

    private val _uiEvents = Channel<UiEvent>(Channel.BUFFERED)
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _imovel = MutableStateFlow<ImovelUiState>(ImovelUiState.Loading)
    val imovel = _imovel.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private var watchImovelJob: Job? = null

    fun init() {
        if (isLoaded) return
        isLoaded = true

        watchImovel(imovelId = route.imovelId)
    }

    private fun watchImovel(imovelId: Long) {
        _isLoading.update { true }

        watchImovelJob = viewModelScope.launch {
            imovelDetailsReadDataSource.watchImovel(imovelId)
                .collect { result ->
                    result
                        .onError { e -> _uiEvents.trySend(UiEvent.ShowErrorBottomSheet(e)) }
                        .onSuccess { storedImovel ->
                            _imovel.update { storedImovel.toUiState() }
                        }
                }
        }
    }

    fun deleteImovel() {
        val imovelId = (imovel.value as? ImovelUiState.Loaded)?.id ?: return

        watchImovelJob?.cancel()

        viewModelScope.launch {
            imovelSharedWriteDataSource.delete(imovelId = imovelId)
                .onError { e -> _uiEvents.trySend(UiEvent.ShowErrorBottomSheet(e)) }
                .onSuccess { _uiEvents.trySend(UiEvent.ShowDeleteSuccessSnackbar) }
        }
    }

    override fun onCleared() {
        _uiEvents.close()

        super.onCleared()
    }

    sealed interface UiEvent {
        data class ShowErrorBottomSheet(val error: BaseError) : UiEvent

        data object NavigateUp : UiEvent

        data object ShowDeleteSuccessSnackbar : UiEvent
    }
}
