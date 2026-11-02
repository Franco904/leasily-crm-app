package com.fstengineering.leasilycrm.imovelDashboard.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.application.repositories.ImovelSharedCommandRepository
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
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
    private val watchImovel: WatchImovelUseCase,
    private val imovelSharedCommandRepository: ImovelSharedCommandRepository,
) : ViewModel() {
    private var isInitialized = false

    private val _uiEvents = Channel<UiEvent>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _imovel = MutableStateFlow<ImovelUiState>(ImovelUiState.Loading)
    val imovel = _imovel.asStateFlow()

    fun init(imovelId: Long) {
        if (isInitialized) return

        watchImovel(imovelId = imovelId)

        isInitialized = true
    }

    private fun watchImovel(imovelId: Long) {
        viewModelScope.launch {
            watchImovel.invoke(ImovelArgs(imovelId)).collect { result ->
                result
                    .onError { e -> _uiEvents.trySend(UiEvent.BottomSheetError(e)) }
                    .onSuccess { storedImovel ->
                        _imovel.update { storedImovel.toUiState() }
                    }
            }
        }
    }

    fun deleteImovel() {
        val imovelId = (imovel.value as? ImovelUiState.Loaded)?.id ?: return

        viewModelScope.launch {
            imovelSharedCommandRepository.delete(id = imovelId)
                .onError { e -> _uiEvents.trySend(UiEvent.BottomSheetError(e)) }
                .onSuccess { _uiEvents.trySend(UiEvent.ImovelDeleteSuccess) }
        }
    }

    sealed interface UiEvent {
        data class BottomSheetError(val error: ApplicationError) : UiEvent
        data object NavigateUp : UiEvent
        data object ImovelDeleteSuccess : UiEvent
    }
}
