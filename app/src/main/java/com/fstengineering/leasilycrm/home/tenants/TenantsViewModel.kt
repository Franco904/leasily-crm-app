package com.fstengineering.leasilycrm.home.tenants

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.application.repositories.TenantRepository
import com.fstengineering.leasilycrm.core.domain.entities.Tenant
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.infra.utils.InfraError
import com.fstengineering.leasilycrm.core.presentation.models.LeadTenantOrderType
import com.fstengineering.leasilycrm.home.tenants.models.TenantFilterType
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
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
    private val tenantRepository: TenantRepository,
) : ViewModel() {
    private var isInitialized = false

    private val _uiEvents = Channel<UiEvents>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _tenants = MutableStateFlow<ImmutableList<Tenant>>(persistentListOf())
    val tenants = _tenants.asStateFlow()

    private val _tenantToDelete = MutableStateFlow<Tenant?>(null)
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
            val orderTypeId = tenantRepository.getPreferredTenantsOrderTypeId()
                .fold(
                    onError = { e -> return@launch _uiEvents.send(UiEvents.Error(e)) },
                    onSuccess = { orderTypeId -> orderTypeId },
                )

            _orderType.update { LeadTenantOrderType.fromId(orderTypeId) }
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

                    tenantRepository.watchAll(
                        tenantStatusFilter = filter?.status,
                        matching = searchInput,
                        orderTypeId = order.id,
                    )
                }
                .collect { result ->
                    result
                        .onError { e -> _uiEvents.send(UiEvents.Error(e)) }
                        .onSuccess { tenants -> _tenants.update { tenants } }

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
            tenantRepository.updatePreferredLeadsOrderTypeId(orderType.id)
        }
    }

    fun onSearchInputChange(searchInput: String?) {
        _searchInput.update { searchInput?.ifEmpty { null } }
    }

    fun onDeleteTenant(tenant: Tenant?) {
        _tenantToDelete.update { tenant }
    }

    fun deleteTenant() {
        viewModelScope.launch {
            val tenantToDelete = this@TenantsViewModel.tenantToDelete.value ?: return@launch

            tenantRepository.delete(tenantToDelete.id!!)
                .onError { e -> return@launch _uiEvents.send(UiEvents.Error(e)) }

            _uiEvents.send(UiEvents.LeadDeleteSuccess)
            onDeleteTenant(tenant = null)
        }
    }

    sealed interface UiEvents {
        data class Error(val error: InfraError) : UiEvents
        data object LeadDeleteSuccess : UiEvents
    }
}
