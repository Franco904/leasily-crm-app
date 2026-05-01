package com.fstengineering.leasilycrm.home.subdomains.leads.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.domain.valueObjects.TenantStatus
import com.fstengineering.leasilycrm.core.presentation.models.LeadTenantOrderType
import com.fstengineering.leasilycrm.core.presentation.models.LeadTenantUiState
import com.fstengineering.leasilycrm.home.application.models.dtos.LeadTenantListItemReadModel
import com.fstengineering.leasilycrm.home.application.repositories.LeadTenantCommandRepository
import com.fstengineering.leasilycrm.home.application.repositories.LeadTenantQueryRepository
import com.fstengineering.leasilycrm.home.presentation.models.LeadTenantListItemUiState
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

class LeadsViewModel(
    private val leadTenantQueryRepository: LeadTenantQueryRepository,
    private val leadTenantCommandRepository: LeadTenantCommandRepository,
) : ViewModel() {
    private var isInitialized = false

    private val _uiEvents = Channel<UiEvent>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _leads =
        MutableStateFlow<ImmutableList<LeadTenantListItemUiState>>(persistentListOf())
    val leads = _leads.asStateFlow()

    private val _leadToDelete = MutableStateFlow<Long?>(null)
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
            val orderTypeId = leadTenantQueryRepository.getPreferredLeadsOrderTypeId()
                .fold(
                    onError = { e ->
                        _uiEvents.trySend(UiEvent.BottomSheetError(e))
                        return@launch
                    },
                    onSuccess = { orderTypeId -> orderTypeId },
                )

            _orderType.update { LeadTenantOrderType.fromId(orderTypeId) }

            leadTenantQueryRepository.watchAll(
                tenantStatusFilter = TenantStatus.Lead,
                orderTypeId = orderTypeId,
            ).collect { result ->
                result
                    .onError { e -> _uiEvents.trySend(UiEvent.BottomSheetError(e)) }
                    .onSuccess { readModels -> _leads.update { readModels.toUiStates() } }

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

                    leadTenantQueryRepository.getAllLeadsMatching(
                        matching = searchInput,
                        orderTypeId = orderType.value.id,
                    )
                        .onError { e -> _uiEvents.trySend(UiEvent.BottomSheetError(e)) }
                        .onSuccess { readModels ->
                            _leads.update { readModels.toUiStates() }
                            _isLoading.update { false }
                        }
                }
        }
    }

    fun onOrderTypeChange(orderType: LeadTenantOrderType) {
        viewModelScope.launch {
            leadTenantQueryRepository.updatePreferredLeadsOrderTypeId(orderType.id)

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
        viewModelScope.launch {
            val leadToDelete = leadToDelete.value ?: return@launch

            leadTenantCommandRepository.delete(leadToDelete)
                .onError { e ->
                    _uiEvents.trySend(UiEvent.BottomSheetError(e))
                    return@launch
                }

            _uiEvents.trySend(UiEvent.LeadDeleteSuccess)
            onDeleteLead(id = null)
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
