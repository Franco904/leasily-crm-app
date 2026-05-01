package com.fstengineering.leasilycrm.home.subdomains.tenants.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.presentation.models.LeadTenantOrderType
import com.fstengineering.leasilycrm.core.presentation.models.LeadTenantUiState
import com.fstengineering.leasilycrm.home.application.models.dtos.LeadTenantListItemReadModel
import com.fstengineering.leasilycrm.home.application.repositories.LeadTenantCommandRepository
import com.fstengineering.leasilycrm.home.application.repositories.LeadTenantQueryRepository
import com.fstengineering.leasilycrm.home.presentation.models.LeadTenantListItemUiState
import com.fstengineering.leasilycrm.home.subdomains.tenants.presentation.models.TenantFilterType
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class TenantsViewModel(
    private val leadTenantQueryRepository: LeadTenantQueryRepository,
    private val leadTenantCommandRepository: LeadTenantCommandRepository,
) : ViewModel() {
    private var isInitialized = false

    private val _uiEvents = Channel<UiEvent>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _tenants =
        MutableStateFlow<ImmutableList<LeadTenantListItemUiState>>(persistentListOf())
    val tenants = _tenants.asStateFlow()

    private val _tenantToDelete = MutableStateFlow<Long?>(null)
    val tenantToDelete = _tenantToDelete.asStateFlow()

    private val _searchInput = MutableStateFlow<String?>(null)

    private val _orderType = MutableStateFlow(LeadTenantOrderType.Name)
    val orderType = _orderType.asStateFlow()

    private val _currentTenantFilterType =
        MutableStateFlow<TenantFilterType?>(TenantFilterType.UpcomingStay)
    val currentTenantFilterType = _currentTenantFilterType.asStateFlow()

    var tenantFilterTypeBeforeSearch = TenantFilterType.UpcomingStay
        private set

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun init() {
        if (isInitialized) return

        getPreferredTenantsOrderTypeId()
        watchTenants()

        isInitialized = true
    }

    fun onPause() {
        isInitialized = false
    }

    private fun getPreferredTenantsOrderTypeId() {
        viewModelScope.launch {
            leadTenantQueryRepository.getPreferredTenantsOrderTypeId()
                .onError { e -> _uiEvents.trySend(UiEvent.BottomSheetError(e)) }
                .onSuccess { orderTypeId ->
                    _orderType.update {
                        LeadTenantOrderType.fromId(
                            orderTypeId
                        )
                    }
                }
        }
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private fun watchTenants() {
        val searchInputDebounced = _searchInput
            .debounce(500.milliseconds)

        viewModelScope.launch {
            orderType
                .combine(currentTenantFilterType) { order, filter -> order to filter }
                .combine(searchInputDebounced) { (order, filter), searchInput ->
                    Triple(order, filter, searchInput)
                }
                .flatMapLatest { (order, filter, searchInput) ->
                    _isLoading.update { true }

                    leadTenantQueryRepository.watchAll(
                        tenantStatusFilter = filter?.status,
                        matching = searchInput,
                        orderTypeId = order.id,
                    )
                }
                .collect { result ->
                    result
                        .onError { e -> _uiEvents.trySend(UiEvent.BottomSheetError(e)) }
                        .onSuccess { readModels -> _tenants.update { readModels.toUiStates() } }

                    _isLoading.update { false }
                }
        }
    }

    fun onTenantFilterTypeChange(tenantFilterType: TenantFilterType?) {
        _currentTenantFilterType.update { tenantFilterType }

        if (tenantFilterType != null) tenantFilterTypeBeforeSearch = tenantFilterType
    }

    fun onOrderTypeChange(orderType: LeadTenantOrderType) {
        _orderType.update { orderType }

        viewModelScope.launch {
            leadTenantQueryRepository.updatePreferredLeadsOrderTypeId(orderType.id)
        }
    }

    fun onSearchInputChange(searchInput: String?) {
        _searchInput.update { searchInput?.ifEmpty { null } }
    }

    fun onDeleteTenant(id: Long?) {
        _tenantToDelete.update { id }
    }

    fun deleteTenant() {
        viewModelScope.launch {
            val tenantToDelete = this@TenantsViewModel.tenantToDelete.value ?: return@launch

            leadTenantCommandRepository.delete(tenantToDelete)
                .onError { e ->
                    _uiEvents.trySend(UiEvent.BottomSheetError(e))
                    return@launch
                }

            _uiEvents.trySend(UiEvent.LeadDeleteSuccess)
            onDeleteTenant(id = null)
        }
    }

    override fun onCleared() {
        _uiEvents.close()

        super.onCleared()
    }

    sealed interface UiEvent {
        data class BottomSheetError(val error: ApplicationError) : UiEvent
        data object LeadDeleteSuccess : UiEvent
    }
}

private fun ImmutableList<LeadTenantListItemReadModel>.toUiStates() =
    map { it.toUiState() }.toPersistentList()

private fun LeadTenantListItemReadModel.toUiState() = LeadTenantListItemUiState(
    leadTenant = LeadTenantUiState(
        id = id,
        name = name,
        city = city,
        guestsCount = guestsCount,
        hasUnfinishedAssistance = hasUnfinishedAssistance,
        hasUnfinishedStay = hasUnfinishedStay,
    ),
    currentStayId = currentStayId,
)
