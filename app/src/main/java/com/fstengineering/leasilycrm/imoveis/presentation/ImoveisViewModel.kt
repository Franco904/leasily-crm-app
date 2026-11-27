package com.fstengineering.leasilycrm.imoveis.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.domain.utils.BaseError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.infra.dataSources.database.ImovelSharedWriteDataSource
import com.fstengineering.leasilycrm.core.presentation.models.ImovelCardUiState
import com.fstengineering.leasilycrm.imoveis.infra.dataSources.ImoveisReadDataSource
import com.fstengineering.leasilycrm.imoveis.presentation.mappers.toUiState
import com.fstengineering.leasilycrm.imoveis.presentation.models.ImoveisUiState
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class ImoveisViewModel(
    private val imoveisReadDataSource: ImoveisReadDataSource,
    private val imovelSharedWriteDataSource: ImovelSharedWriteDataSource,
) : ViewModel() {
    private var isLoaded = false

    private val _uiEvents = Channel<UiEvent>(Channel.BUFFERED)
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _uiState = MutableStateFlow<ImoveisUiState>(ImoveisUiState.Loading)
    val uiState: StateFlow<ImoveisUiState> = _uiState.asStateFlow()

    private val _imovelToDelete = MutableStateFlow<ImovelCardUiState?>(null)
    val imovelToDelete = _imovelToDelete.asStateFlow()

    private val _searchInput = MutableStateFlow<String?>(null)
    val searchInput = _searchInput.asStateFlow()

    fun init() {
        if (isLoaded) return
        isLoaded = true

        watchImoveis()
        watchSearchInput()
    }

    private fun watchImoveis() {
        viewModelScope.launch {
            imoveisReadDataSource.watchImoveis().collectLatest { result ->
                result
                    .onError { e -> _uiEvents.trySend(UiEvent.ShowErrorBottomSheet(e)) }
                    .onSuccess { readModels ->
                        val imoveis = readModels.map { it.toUiState() }.toPersistentList()
                        _uiState.update { ImoveisUiState.Loaded(imoveis = imoveis) }
                    }
            }
        }
    }

    @OptIn(FlowPreview::class)
    private fun watchSearchInput() {
        viewModelScope.launch {
            searchInput
                .debounce(500.milliseconds)
                .collectLatest { searchInput ->
                    imoveisReadDataSource.getImoveis(matching = searchInput)
                        .onError { e -> _uiEvents.trySend(UiEvent.ShowErrorBottomSheet(e)) }
                        .onSuccess { readModels ->
                            val imoveis = readModels.map { it.toUiState() }.toPersistentList()
                            _uiState.update { ImoveisUiState.Loaded(imoveis = imoveis) }
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
        val imovelId = imovelToDelete.value?.id ?: return

        viewModelScope.launch {
            imovelSharedWriteDataSource.delete(imovelId = imovelId)
                .onError { e -> _uiEvents.trySend(UiEvent.ShowErrorBottomSheet(e)) }
                .onSuccess {
                    _uiEvents.trySend(UiEvent.ShowDeleteSuccessSnackbar)

                    onDeleteImovel(imovel = null)
                }
        }
    }

    sealed interface UiEvent {
        data class ShowErrorBottomSheet(val error: BaseError) : UiEvent
        data object ShowDeleteSuccessSnackbar : UiEvent
    }
}
