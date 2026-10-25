package com.fstengineering.leasilycrm.proprietarios.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.presentation.models.ProprietarioUiState
import com.fstengineering.leasilycrm.proprietarioRegistration.infra.dataSources.ProprietarioWriteDataSource
import com.fstengineering.leasilycrm.proprietarios.infra.dataSources.ProprietariosReadDataSource
import com.fstengineering.leasilycrm.proprietarios.presentation.mappers.toProprietarioUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProprietariosViewModel(
    private val proprietariosReadDataSource: ProprietariosReadDataSource,
    private val proprietarioWriteDataSource: ProprietarioWriteDataSource,
) : ViewModel() {
    private var isLoaded = false

    private val _uiEvents = Channel<UiEvent>(Channel.BUFFERED)
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _proprietarios =
        MutableStateFlow<ImmutableList<ProprietarioUiState>>(persistentListOf())
    val proprietarios = _proprietarios.asStateFlow()

    private val _proprietarioToDelete = MutableStateFlow<ProprietarioUiState?>(null)
    val proprietarioToDelete = _proprietarioToDelete.asStateFlow()

    fun init() {
        if (isLoaded) return
        isLoaded = true

        viewModelScope.launch {
            proprietariosReadDataSource.watchProprietarios().collect { result ->
                result
                    .onError { e -> _uiEvents.trySend(UiEvent.BottomSheetError(e)) }
                    .onSuccess { readModels ->
                        val proprietarios =
                            readModels.map { it.toProprietarioUiState() }.toPersistentList()
                        _proprietarios.update { proprietarios }
                    }
            }
        }
    }

    fun onDeleteProprietario(proprietario: ProprietarioUiState?) {
        _proprietarioToDelete.update { proprietario }
    }

    fun deleteProprietario() {
        val proprietarioId = proprietarioToDelete.value?.id ?: return

        viewModelScope.launch {
            proprietarioWriteDataSource.delete(proprietarioId = proprietarioId)
                .onError { e -> _uiEvents.trySend(UiEvent.BottomSheetError(e)) }
                .onSuccess {
                    _uiEvents.trySend(UiEvent.ProprietarioDeleteSuccess)

                    onDeleteProprietario(proprietario = null)
                }
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
