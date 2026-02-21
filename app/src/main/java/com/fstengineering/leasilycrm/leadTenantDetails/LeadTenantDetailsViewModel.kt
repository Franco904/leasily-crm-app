package com.fstengineering.leasilycrm.leadTenantDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.domain.entities.Assistance
import com.fstengineering.leasilycrm.core.domain.entities.AssistanceCharacteristic
import com.fstengineering.leasilycrm.core.domain.entities.Tenant
import com.fstengineering.leasilycrm.core.application.repositories.AssistanceRepository
import com.fstengineering.leasilycrm.core.application.repositories.TenantRepository
import com.fstengineering.leasilycrm.core.infra.utils.InfraError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LeadTenantDetailsViewModel(
    private val tenantRepository: TenantRepository,
    private val assistanceRepository: AssistanceRepository,
) : ViewModel() {
    private var isInitialized = false

    private val _uiEvents = Channel<UiEvents>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _leadTenant = MutableStateFlow(Tenant())
    val leadTenant = _leadTenant.asStateFlow()

    private val _assistancePropertyCharacteristics =
        MutableStateFlow<ImmutableList<AssistanceCharacteristic>>(persistentListOf())
    val assistancePropertyCharacteristics = _assistancePropertyCharacteristics.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private var watchTenantJob: Job? = null

    fun init(propertyId: Long) {
        if (isInitialized) return

        watchLeadTenant(leadTenantId = propertyId)
        getAssistancePropertyCharacteristics()

        isInitialized = true
    }

    private fun watchLeadTenant(leadTenantId: Long) {
        _isLoading.update { true }

        watchTenantJob = viewModelScope.launch {
            tenantRepository.watchById(leadTenantId).collect { result ->
                result
                    .onError { e -> _uiEvents.send(UiEvents.Error(e)) }
                    .onSuccess { storedLeadTenant ->
                        if (storedLeadTenant == null) {
                            _uiEvents.send(UiEvents.NavigateUp)
                            watchTenantJob?.cancel()
                            return@onSuccess
                        }

                        _leadTenant.update { storedLeadTenant }
                    }
            }
        }
    }

    private fun getAssistancePropertyCharacteristics() {
        viewModelScope.launch {
            assistanceCharacteristicRepository.getAll()
                .onError { e -> _uiEvents.send(UiEvents.Error(e)) }
                .onSuccess { storedCharacteristics ->
                    _assistancePropertyCharacteristics.update { storedCharacteristics }
                }

            _isLoading.update { false }
        }
    }

    fun deleteLeadTenant() {
        val leadTenantId = leadTenant.value.id ?: return

        watchTenantJob?.cancel()

        viewModelScope.launch {
            tenantRepository.delete(id = leadTenantId)
                .onError { e -> return@launch _uiEvents.send(UiEvents.Error(e)) }

            _uiEvents.send(UiEvents.LeadTenantDeleteSuccess)
        }
    }

    fun deleteCurrentAssistance(currentAssistance: Assistance) {
        viewModelScope.launch {
            assistanceRepository.delete(id = currentAssistance.id!!)
                .onError { e -> return@launch _uiEvents.send(UiEvents.Error(e)) }

            _uiEvents.send(UiEvents.CurrentAssistanceDeleteSuccess)
        }
    }

    sealed interface UiEvents {
        data class Error(val error: InfraError) : UiEvents

        data object NavigateUp : UiEvents

        data object LeadTenantDeleteSuccess : UiEvents

        data object CurrentAssistanceDeleteSuccess : UiEvents
    }
}
