package com.fstengineering.leasilycrm.atendimentoRegistration.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.atendimentoRegistration.infra.dataSources.ImovelReadDataSource
import com.fstengineering.leasilycrm.atendimentoRegistration.presentation.mappers.toImovelCardUiState
import com.fstengineering.leasilycrm.core.domain.utils.BaseError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.presentation.models.ImovelCardUiState
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AtendimentoRegistrationStep2ViewModel(
    private val imovelReadDataSource: ImovelReadDataSource,
) : ViewModel() {
    private var isLoaded = false

    private val _uiEvents = Channel<UiEvent>(Channel.BUFFERED)
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _imoveis =
        MutableStateFlow<ImmutableList<ImovelCardUiState>>(persistentListOf())
    val imoveis = _imoveis.asStateFlow()

    private val _searchInput = MutableStateFlow<String?>(null)
    val searchInput = _searchInput.asStateFlow()

    fun init() {
        if (isLoaded) return
        isLoaded = true

        viewModelScope.launch { loadImoveis(matching = null) }
        watchSearchInput()
    }

    @OptIn(FlowPreview::class)
    private fun watchSearchInput() {
        viewModelScope.launch {
            searchInput
                .drop(1)
                .debounce(500.milliseconds)
                .collectLatest { searchInput -> loadImoveis(matching = searchInput) }
        }
    }

    private suspend fun loadImoveis(matching: String?) {
        imovelReadDataSource.getImoveis(matching)
            .onError { e -> _uiEvents.trySend(UiEvent.ShowErrorBottomSheet(e)) }
            .onSuccess { imoveis ->
                val uiStates = imoveis.map { it.toImovelCardUiState() }.toImmutableList()

                _imoveis.update { uiStates }
            }
    }

    fun onSearchInputChange(searchInput: String?) {
        _searchInput.update { searchInput?.ifBlank { null } }
    }

    override fun onCleared() {
        _uiEvents.close()

        super.onCleared()
    }

    sealed interface UiEvent {
        data class ShowErrorBottomSheet(val error: BaseError) : UiEvent
    }
}
