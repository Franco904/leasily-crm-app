package com.fstengineering.leasilycrm.proprietarios.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.application.repositories.ProprietarioSharedCommandRepository
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.presentation.models.ProprietarioUiState
import com.fstengineering.leasilycrm.proprietarios.application.repositories.ProprietarioQueryRepository
import com.fstengineering.leasilycrm.proprietarios.presentation.mappers.toUiState
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
    private val proprietarioQueryRepository: ProprietarioQueryRepository,
    private val proprietarioSharedCommandRepository: ProprietarioSharedCommandRepository,
) : ViewModel() {
    private var isInitialized = false

    private val _uiEvents = Channel<UiEvent>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _proprietarios =
        MutableStateFlow<ImmutableList<ProprietarioUiState>>(persistentListOf())
    val proprietarios = _proprietarios.asStateFlow()

    private val _proprietarioToDelete = MutableStateFlow<ProprietarioUiState?>(null)
    val proprietarioToDelete = _proprietarioToDelete.asStateFlow()

    fun init() {
        if (isInitialized) return

        watchProprietarios()

        isInitialized = true
    }

    private fun watchProprietarios() {
        viewModelScope.launch {
            proprietarioQueryRepository.watchAll().collect { result ->
                result
                    .onError { e -> _uiEvents.send(UiEvent.BottomSheetError(e)) }
                    .onSuccess { proprietarios ->
                        val states = proprietarios.map { it.toUiState() }.toPersistentList()
                        _proprietarios.update { states }
                    }
            }
        }
    }

    fun onDeleteProprietario(proprietario: ProprietarioUiState?) {
        _proprietarioToDelete.update { proprietario }
    }

    fun deleteProprietario() {
        viewModelScope.launch {
            val id = proprietarioToDelete.value?.id ?: return@launch

            proprietarioSharedCommandRepository.delete(id)
                .onError { e -> _uiEvents.send(UiEvent.BottomSheetError(e)) }
                .onSuccess {
                    _uiEvents.send(UiEvent.ProprietarioDeleteSuccess)

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
