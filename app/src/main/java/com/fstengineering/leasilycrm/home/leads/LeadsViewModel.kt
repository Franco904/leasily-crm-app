package com.fstengineering.leasilycrm.home.leads

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.application.repositories.LeadTenantSharedCommandRepository
import com.fstengineering.leasilycrm.core.domain.entities.Tenant
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.domain.valueObjects.TenantStatus
import com.fstengineering.leasilycrm.core.infra.utils.InfraError
import com.fstengineering.leasilycrm.core.presentation.models.LeadTenantOrderType
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class LeadsViewModel(
    private val leadTenantSharedCommandRepository: LeadTenantSharedCommandRepository,
) : ViewModel() {
    private var isInitialized = false

    private val _uiEvents = Channel<UiEvents>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _leads = MutableStateFlow<ImmutableList<Tenant>>(persistentListOf())
    val leads = _leads.asStateFlow()

    private val _leadToDelete = MutableStateFlow<Tenant?>(null)
    val leadToDelete = _leadToDelete.asStateFlow()

    private val _searchInput = MutableStateFlow<String?>(null)

    private val _orderType = MutableStateFlow(LeadTenantOrderType.Name)
    val orderType = _orderType.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun init() {
        if (isInitialized) return

        watchLeads()
        watchSearchInput()

        isInitialized = true
    }

    fun onPause() {
        isInitialized = false
    }

    private fun watchLeads() {
        _isLoading.update { true }

        viewModelScope.launch {
            val orderTypeId = leadTenantSharedCommandRepository.getPreferredLeadsOrderTypeId()
                .fold(
                    onError = { e -> return@launch _uiEvents.send(UiEvents.Error(e)) },
                    onSuccess = { orderTypeId -> orderTypeId },
                )

            _orderType.update { LeadTenantOrderType.fromId(orderTypeId) }

            leadTenantSharedCommandRepository.watchAll(
                tenantStatusFilter = TenantStatus.Lead,
                orderTypeId = orderTypeId,
            ).collect { result ->
                result
                    .onError { e -> _uiEvents.send(UiEvents.Error(e)) }
                    .onSuccess { leads -> _leads.update { leads } }

                _isLoading.update { false }
            }
        }
    }

    @OptIn(FlowPreview::class)
    private fun watchSearchInput() {
        viewModelScope.launch {
            _searchInput
                .debounce(500.milliseconds)
                .collect { searchInput ->
                    _isLoading.update { true }

                    leadTenantSharedCommandRepository.getAllLeadsMatching(
                        matching = searchInput,
                        orderTypeId = orderType.value.id,
                    )
                        .onError { e -> _uiEvents.send(UiEvents.Error(e)) }
                        .onSuccess { matchingLeads ->
                            _leads.update { matchingLeads }
                            _isLoading.update { false }
                        }
                }
        }
    }

    fun onOrderTypeChange(orderType: LeadTenantOrderType) {
        viewModelScope.launch {
            leadTenantSharedCommandRepository.updatePreferredLeadsOrderTypeId(orderType.id)

            watchLeads()
        }
    }

    fun onSearchInputChange(searchInput: String?) {
        _searchInput.update { searchInput?.ifEmpty { null } }
    }

    fun onDeleteLead(lead: Tenant?) {
        _leadToDelete.update { lead }
    }

    fun deleteLead() {
        viewModelScope.launch {
            val leadToDelete = leadToDelete.value ?: return@launch

            leadTenantSharedCommandRepository.delete(leadToDelete.id!!)
                .onError { e -> return@launch _uiEvents.send(UiEvents.Error(e)) }

            _uiEvents.send(UiEvents.LeadDeleteSuccess)
            onDeleteLead(lead = null)
        }
    }

    sealed interface UiEvents {
        data class Error(val error: InfraError) : UiEvents

        data object LeadDeleteSuccess : UiEvents
    }
}
