package com.fstengineering.leasilycrm.imovelDetails.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.application.repositories.ImovelSharedCommandRepository
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.imovelDetails.application.repositories.ImovelQueryRepository
import com.fstengineering.leasilycrm.imovelDetails.presentation.mappers.toUiState
import com.fstengineering.leasilycrm.imovelDetails.presentation.models.ImovelUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ImovelDetailsViewModel(
    private val imovelQueryRepository: ImovelQueryRepository,
    private val imovelSharedCommandRepository: ImovelSharedCommandRepository,
) : ViewModel() {
    private var isInitialized = false

    private val _uiEvents = Channel<UiEvent>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _property = MutableStateFlow<ImovelUiState>(ImovelUiState.Loading)
    val imovel = _property.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private var watchImovelJob: Job? = null

    fun init(imovelId: Long) {
        if (isInitialized) return

        watchImovel(imovelId = imovelId)

        isInitialized = true
    }

    private fun watchImovel(imovelId: Long) {
        _isLoading.update { true }

        watchImovelJob = viewModelScope.launch {
            imovelQueryRepository.watchById(imovelId)
                .collectLatest { result ->
                    result
                        .onError { e -> _uiEvents.trySend(UiEvent.BottomSheetError(e)) }
                        .onSuccess { storedImovel ->
                            _property.update { storedImovel.toUiState() }
                        }
                }
        }
    }

    fun deleteImovel() {
        val imovelId = (imovel.value as? ImovelUiState.Loaded)?.id ?: return

        watchImovelJob?.cancel()

        viewModelScope.launch {
            imovelSharedCommandRepository.delete(id = imovelId)
                .onError { e -> _uiEvents.trySend(UiEvent.BottomSheetError(e)) }
                .onSuccess { _uiEvents.trySend(UiEvent.ImovelDeleteSuccess) }
        }
    }

    override fun onCleared() {
        _uiEvents.close()

        super.onCleared()
    }

    sealed interface UiEvent {
        data class BottomSheetError(val error: ApplicationError) : UiEvent

        data object NavigateUp : UiEvent

        data object ImovelDeleteSuccess : UiEvent
    }
}
