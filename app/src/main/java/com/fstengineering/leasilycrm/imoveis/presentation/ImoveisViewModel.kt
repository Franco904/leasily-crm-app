package com.fstengineering.leasilycrm.imoveis.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.application.repositories.ImovelSharedCommandRepository
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.presentation.models.ImovelCardUiState
import com.fstengineering.leasilycrm.imoveis.application.repositories.ImovelQueryRepository
import com.fstengineering.leasilycrm.imoveis.presentation.mappers.toUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class ImoveisViewModel(
    private val imovelQueryRepository: ImovelQueryRepository,
    private val imovelSharedCommandRepository: ImovelSharedCommandRepository,
) : ViewModel() {
    private var isInitialized = false

    private val _uiEvents = Channel<UiEvent>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _imoveis =
        MutableStateFlow<ImmutableList<ImovelCardUiState>>(persistentListOf())
    val imoveis = _imoveis.asStateFlow()

    private val _imovelToDelete = MutableStateFlow<ImovelCardUiState?>(null)
    val imovelToDelete = _imovelToDelete.asStateFlow()

    private val _searchInput = MutableStateFlow<String?>(null)
    val searchInput = _searchInput.asStateFlow()

    fun init() {
        if (isInitialized) return

        watchImoveis()
        watchSearchInput()

        isInitialized = true
    }

    private fun watchImoveis() {
        viewModelScope.launch {
            imovelQueryRepository.watchAll().collect { result ->
                result
                    .onError { e -> _uiEvents.trySend(UiEvent.BottomSheetError(e)) }
                    .onSuccess { storedImoveis ->
                        val imoveis = storedImoveis.map { it.toUiState() }.toPersistentList()
                        _imoveis.update { imoveis }
                    }
            }
        }
    }

    @OptIn(FlowPreview::class)
    fun watchSearchInput() {
        viewModelScope.launch {
            searchInput
                .debounce(500.milliseconds)
                .collect { searchInput ->
                    imovelQueryRepository.getAllMatching(matching = searchInput)
                        .onError { e -> _uiEvents.trySend(UiEvent.BottomSheetError(e)) }
                        .onSuccess { matchingImoveis ->
                            val imoveis =
                                matchingImoveis.map { it.toUiState() }.toPersistentList()
                            _imoveis.update { imoveis }
                        }
                }
        }
    }

    fun onSearchInputChange(searchInput: String?) {
        _searchInput.update { searchInput?.ifBlank { null } }
    }

    fun onDeleteImovel(imovel: ImovelCardUiState?) {
        _imovelToDelete.update { imovel }
    }

    fun deleteImovel() {
        viewModelScope.launch {
            val id = imovelToDelete.value?.id ?: return@launch

            imovelSharedCommandRepository.delete(id)
                .onError { e -> _uiEvents.trySend(UiEvent.BottomSheetError(e)) }
                .onSuccess {
                    _uiEvents.trySend(UiEvent.ImovelDeleteSuccess)

                    onDeleteImovel(imovel = null)
                }
        }
    }

    override fun onCleared() {
        _uiEvents.close()

        super.onCleared()
    }

    sealed interface UiEvent {
        data class BottomSheetError(val error: ApplicationError) : UiEvent
        data object ImovelDeleteSuccess : UiEvent
    }
}
