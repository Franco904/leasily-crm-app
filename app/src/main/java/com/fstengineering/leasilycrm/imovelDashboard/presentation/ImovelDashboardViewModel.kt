package com.fstengineering.leasilycrm.imovelDashboard.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.Destinations
import com.fstengineering.leasilycrm.core.domain.utils.BaseError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.infra.dataSources.database.ImovelSharedWriteDataSource
import com.fstengineering.leasilycrm.imovelDashboard.application.models.queries.ImovelArgs
import com.fstengineering.leasilycrm.imovelDashboard.application.useCases.WatchImovelUseCase
import com.fstengineering.leasilycrm.imovelDashboard.presentation.mappers.toUiState
import com.fstengineering.leasilycrm.imovelDashboard.presentation.models.ImovelUiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ImovelDashboardViewModel(
    private val route: Destinations.ImovelDashboard,
    private val watchImovel: WatchImovelUseCase,
    private val imovelSharedWriteDataSource: ImovelSharedWriteDataSource,
) : ViewModel() {
    private var isLoaded = false

    private val _uiEvents = Channel<UiEvent>(Channel.BUFFERED)
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _imovel = MutableStateFlow<ImovelUiState>(ImovelUiState.Loading)
    val imovel = _imovel.asStateFlow()

    fun init() {
        if (isLoaded) return
        isLoaded = true

        watchImovel(imovelId = route.imovelId)
    }

    private fun watchImovel(imovelId: Long) {
        viewModelScope.launch {
            watchImovel.invoke(ImovelArgs(imovelId)).collect { result ->
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
