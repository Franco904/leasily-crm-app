package com.fstengineering.leasilycrm.clientes.presentation.hospedes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.clienteRegistration.infra.dataSources.ClienteWriteDataSource
import com.fstengineering.leasilycrm.clientes.infra.dataSources.ClientesReadDataSource
import com.fstengineering.leasilycrm.clientes.infra.localStorage.ClientesLocalStorage
import com.fstengineering.leasilycrm.clientes.presentation.mappers.toClienteListItemUiState
import com.fstengineering.leasilycrm.clientes.presentation.models.ClienteListItemUiState
import com.fstengineering.leasilycrm.clientes.presentation.models.HospedeFilterType
import com.fstengineering.leasilycrm.core.domain.utils.BaseError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.presentation.models.ClienteOrderType
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class HospedesViewModel(
    private val clientesReadDataSource: ClientesReadDataSource,
    private val clienteWriteDataSource: ClienteWriteDataSource,
    private val clientesLocalStorage: ClientesLocalStorage,
) : ViewModel() {
    private var isLoaded = false

    private val _uiEvents = Channel<UiEvent>(Channel.BUFFERED)
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _inquilinos =
        MutableStateFlow<ImmutableList<ClienteListItemUiState>>(persistentListOf())
    val hospedes = _inquilinos.asStateFlow()

    private val _hospedeToDelete = MutableStateFlow<Long?>(null)
    val hospedeToDelete = _hospedeToDelete.asStateFlow()

    private val _searchInput = MutableStateFlow<String?>(null)

    private val _orderType = MutableStateFlow(ClienteOrderType.Name)
    val orderType = _orderType.asStateFlow()

    private val _currentHospedeFilterType =
        MutableStateFlow<HospedeFilterType?>(HospedeFilterType.ComEstadia)
    val currentHospedeFilterType = _currentHospedeFilterType.asStateFlow()

    var hospedeFilterTypeBeforeSearch = HospedeFilterType.ComEstadia
        private set

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun init() {
        if (isLoaded) return
        isLoaded = true

        loadPreferredOrderType()
        watchHospedes()
    }

    private fun loadPreferredOrderType() {
        viewModelScope.launch {
            clientesLocalStorage.getHospedesOrderTypeId()
                .onError { e -> _uiEvents.trySend(UiEvent.ShowErrorBottomSheet(e)) }
                .onSuccess { orderTypeId ->
                    _orderType.update { ClienteOrderType.fromId(orderTypeId) }
                }
        }
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private fun watchHospedes() {
        val searchInputDebounced = _searchInput
            .debounce(500.milliseconds)

        viewModelScope.launch {
            orderType
                .combine(currentHospedeFilterType) { order, filter -> order to filter }
                .combine(searchInputDebounced) { (order, filter), searchInput ->
                    Triple(order, filter, searchInput)
                }
                .flatMapLatest { (order, filter, searchInput) ->
                    _isLoading.update { true }

                    clientesReadDataSource.watchClientes(
                        status = filter?.status,
                        matching = searchInput,
                        orderTypeId = order.id,
                    )
                }
                .collectLatest { result ->
                    result
                        .onError { e -> _uiEvents.trySend(UiEvent.ShowErrorBottomSheet(e)) }
                        .onSuccess { readModels ->
                            _inquilinos.update {
                                readModels.map { it.toClienteListItemUiState() }.toPersistentList()
                            }
                        }

                    _isLoading.update { false }
                }
        }
    }

    fun onHospedeFilterTypeChange(hospedeFilterType: HospedeFilterType?) {
        _currentHospedeFilterType.update { hospedeFilterType }

        if (hospedeFilterType != null) hospedeFilterTypeBeforeSearch = hospedeFilterType
    }

    fun onOrderTypeChange(orderType: ClienteOrderType) {
        _orderType.update { orderType }

        viewModelScope.launch {
            clientesLocalStorage.saveHospedesOrderTypeId(orderType.id)
                .onError { e -> _uiEvents.trySend(UiEvent.ShowErrorBottomSheet(e)) }
        }
    }

    fun onSearchInputChange(searchInput: String?) {
        _searchInput.update { searchInput?.ifEmpty { null } }
    }

    fun onDeleteHospede(id: Long?) {
        _hospedeToDelete.update { id }
    }

    fun deleteHospede() {
        val hospedeToDelete = hospedeToDelete.value ?: return

        viewModelScope.launch {
            clienteWriteDataSource.delete(hospedeToDelete)
                .onError { e ->
                    _uiEvents.trySend(UiEvent.ShowErrorBottomSheet(e))
                    return@launch
                }

            _uiEvents.trySend(UiEvent.ShowDeleteSuccessSnackbar)
            onDeleteHospede(id = null)
        }
    }

    sealed interface UiEvent {
        data class ShowErrorBottomSheet(val error: BaseError) : UiEvent
        data object ShowDeleteSuccessSnackbar : UiEvent
    }
}
