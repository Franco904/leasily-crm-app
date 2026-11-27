package com.fstengineering.leasilycrm.clientes.presentation.leads

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.clienteRegistration.infra.dataSources.ClienteWriteDataSource
import com.fstengineering.leasilycrm.clientes.infra.dataSources.ClientesReadDataSource
import com.fstengineering.leasilycrm.clientes.infra.localStorage.ClientesLocalStorage
import com.fstengineering.leasilycrm.clientes.presentation.mappers.toClienteListItemUiState
import com.fstengineering.leasilycrm.clientes.presentation.models.ClienteListItemUiState
import com.fstengineering.leasilycrm.core.domain.utils.BaseError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.domain.valueObjects.ClienteStatus
import com.fstengineering.leasilycrm.core.presentation.models.ClienteOrderType
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class LeadsViewModel(
    private val clientesReadDataSource: ClientesReadDataSource,
    private val clienteWriteDataSource: ClienteWriteDataSource,
    private val clientesLocalStorage: ClientesLocalStorage,
) : ViewModel() {
    private var isLoaded = false

    private val _uiEvents = Channel<UiEvent>(Channel.BUFFERED)
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _leads = MutableStateFlow<ImmutableList<ClienteListItemUiState>>(persistentListOf())
    val leads = _leads.asStateFlow()

    private val _leadToDelete = MutableStateFlow<Long?>(null)
    val leadToDelete = _leadToDelete.asStateFlow()

    private val _searchInput = MutableStateFlow<String?>(null)

    private val _orderType = MutableStateFlow(ClienteOrderType.Name)
    val orderType = _orderType.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private var watchLeadsJob: Job? = null

    fun init() {
        if (isLoaded) return
        isLoaded = true

        watchLeads()
        watchSearchInput()
    }

    private fun watchLeads() {
        _isLoading.update { true }

        watchLeadsJob?.cancel()
        watchLeadsJob = viewModelScope.launch {
            val orderTypeId = clientesLocalStorage.getLeadsOrderTypeId()
                .fold(
                    onError = { e ->
                        _uiEvents.trySend(UiEvent.ShowErrorBottomSheet(e))
                        return@launch
                    },
                    onSuccess = { orderTypeId -> orderTypeId },
                )

            _orderType.update { ClienteOrderType.fromId(orderTypeId) }

            clientesReadDataSource.watchClientes(
                status = ClienteStatus.Lead,
                orderTypeId = orderTypeId,
            ).collectLatest { result ->
                result
                    .onError { e -> _uiEvents.trySend(UiEvent.ShowErrorBottomSheet(e)) }
                    .onSuccess { readModels ->
                        _leads.update {
                            readModels.map { it.toClienteListItemUiState() }.toPersistentList()
                        }
                    }

                _isLoading.update { false }
            }
        }
    }

    @OptIn(FlowPreview::class)
    private fun watchSearchInput() {
        viewModelScope.launch {
            _searchInput
                .debounce(500.milliseconds)
                .collectLatest { searchInput ->
                    _isLoading.update { true }

                    clientesReadDataSource.getLeads(
                        matching = searchInput,
                        orderTypeId = orderType.value.id,
                    )
                        .onError { e -> _uiEvents.trySend(UiEvent.ShowErrorBottomSheet(e)) }
                        .onSuccess { readModels ->
                            _leads.update {
                                readModels.map { it.toClienteListItemUiState() }.toPersistentList()
                            }
                            _isLoading.update { false }
                        }
                }
        }
    }

    fun onOrderTypeChange(orderType: ClienteOrderType) {
        viewModelScope.launch {
            clientesLocalStorage.saveLeadsOrderTypeId(orderType.id)
                .onError { e -> _uiEvents.trySend(UiEvent.ShowErrorBottomSheet(e)) }

            watchLeads()
        }
    }

    fun onSearchInputChange(searchInput: String?) {
        _searchInput.update { searchInput?.ifEmpty { null } }
    }

    fun onDeleteLead(id: Long?) {
        _leadToDelete.update { id }
    }

    fun deleteLead() {
        val leadToDelete = leadToDelete.value ?: return

        viewModelScope.launch {
            clienteWriteDataSource.delete(leadToDelete)
                .onError { e ->
                    _uiEvents.trySend(UiEvent.ShowErrorBottomSheet(e))
                    return@launch
                }

            _uiEvents.trySend(UiEvent.ShowDeleteSuccessSnackbar)
            onDeleteLead(id = null)
        }
    }

    sealed interface UiEvent {
        data class ShowErrorBottomSheet(val error: BaseError) : UiEvent
        data object ShowDeleteSuccessSnackbar : UiEvent
    }
}
