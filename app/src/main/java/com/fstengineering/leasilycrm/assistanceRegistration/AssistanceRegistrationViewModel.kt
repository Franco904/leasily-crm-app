package com.fstengineering.leasilycrm.assistanceRegistration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.entities.Assistance
import com.fstengineering.leasilycrm.core.domain.entities.AssistanceCharacteristic
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertySeaProximityType
import com.fstengineering.leasilycrm.core.domain.repositories.AssistanceRepository
import com.fstengineering.leasilycrm.core.domain.repositories.TenantRepository
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AssistanceRegistrationViewModel(
    private val assistanceRepository: AssistanceRepository,
    private val tenantRepository: TenantRepository,
    private val propertySeaProximityTypeRepository: PropertySeaProximityTypeRepository,
    private val assistanceCharacteristicRepository: AssistanceCharacteristicRepository,
) : ViewModel() {
    private var isInitialized = false

    var assistanceId: Long? = null
        private set

    private val _uiEvents = Channel<UiEvents>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _pastNeighborhoods =
        MutableStateFlow<ImmutableList<String>>(persistentListOf())
    val pastNeighborhoods = _pastNeighborhoods.asStateFlow()

    private val _seaProximityTypes =
        MutableStateFlow<ImmutableList<PropertySeaProximityType>>(persistentListOf())
    val seaProximityTypes = _seaProximityTypes.asStateFlow()

    private val _assistancePropertyCharacteristics =
        MutableStateFlow<ImmutableList<AssistanceCharacteristic>>(persistentListOf())
    val assistancePropertyCharacteristics = _assistancePropertyCharacteristics.asStateFlow()

    private val _assistanceForm = MutableStateFlow(Assistance())
    val assistanceForm = _assistanceForm.asStateFlow()

    private var assistanceToEdit: Assistance? = null

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun init(
        assistanceId: Long?,
        leadTenantId: Long,
    ) {
        if (isInitialized) return

        _isLoading.update { true }

        if (assistanceId != null) {
            getAssistanceToEdit(assistanceId)
            this.assistanceId = assistanceId
        } else {
            getAssistanceLeadTenant(leadTenantId = leadTenantId)
        }

        getPastNeighborhoods()
        getSeaProximityTypes()
        getAssistancePropertyCharacteristics()

        isInitialized = true
    }

    private fun getAssistanceToEdit(assistanceId: Long) {
        viewModelScope.launch {
            assistanceRepository.getById(assistanceId)
                .onError { e -> _uiEvents.send(UiEvents.Error(e)) }
                .onSuccess { storedAssistance ->
                    _assistanceForm.update { storedAssistance }
                    assistanceToEdit = storedAssistance
                }

            _isLoading.update { false }
        }
    }

    private fun getAssistanceLeadTenant(leadTenantId: Long) {
        viewModelScope.launch {
            tenantRepository.getById(tenantId = leadTenantId)
                .onError { e -> _uiEvents.send(UiEvents.Error(e)) }
                .onSuccess { storedLeadTenant ->
                    _assistanceForm.update { it.copy(tenant = storedLeadTenant) }
                }

            _isLoading.update { false }
        }
    }

    private fun getPastNeighborhoods() {
        viewModelScope.launch {
            assistanceRepository.getAllNeighborhoodNames()
                .onError { e -> _uiEvents.send(UiEvents.Error(e)) }
                .onSuccess { neighborhoodNames -> _pastNeighborhoods.update { neighborhoodNames } }

            _isLoading.update { false }
        }
    }

    private fun getSeaProximityTypes() {
        viewModelScope.launch {
            propertySeaProximityTypeRepository.getAll()
                .onError { e -> _uiEvents.send(UiEvents.Error(e)) }
                .onSuccess { storedSeaProximityTypes ->
                    _seaProximityTypes.update { storedSeaProximityTypes }
                }
        }
    }

    private fun getAssistancePropertyCharacteristics() {
        viewModelScope.launch {
            assistanceCharacteristicRepository.getAll()
                .onError { e -> _uiEvents.send(UiEvents.Error(e)) }
                .onSuccess { storedAssistanceCharacteristics ->
                    _assistancePropertyCharacteristics.update { storedAssistanceCharacteristics }
                }

            _isLoading.update { false }
        }
    }

    fun onAssistanceFormChange(form: Assistance) {
        _assistanceForm.update { form }
    }

    fun saveAssistance() {
        viewModelScope.launch {
            if (assistanceToEdit != null && assistanceForm.value == assistanceToEdit) {
                _uiEvents.send(UiEvents.AssistanceEditSuccess(messageId = R.string.no_data_changed_success_message))
                return@launch
            }

            assistanceRepository.insertOrUpdate(assistanceForm.value)
                .onError { e -> return@launch _uiEvents.send(UiEvents.Error(e)) }

            val saveEvent =
                if (assistanceToEdit != null) UiEvents.AssistanceEditSuccess(messageId = R.string.edit_assistance_success_message)
                else UiEvents.AssistanceCreateSuccess

            _uiEvents.send(saveEvent)

            if (assistanceForm.value.tenant?.isOld == true) {
                _uiEvents.send(UiEvents.ConvertOldTenantToLead)
            }
        }
    }

    fun onExit() {
        val needConfirmation = when {
            assistanceToEdit != null -> assistanceToEdit != assistanceForm.value
            else -> assistanceForm.value.isAnyFormFieldInitialized
        }

        viewModelScope.launch {
            _uiEvents.send(UiEvents.Exit(needConfirmation))
        }
    }

    sealed interface UiEvents {
        data class Error(val error: DataError) : UiEvents

        data object AssistanceCreateSuccess : UiEvents

        data class AssistanceEditSuccess(val messageId: Int) : UiEvents

        data object ConvertOldTenantToLead : UiEvents

        data class Exit(val needConfirmation: Boolean) : UiEvents
    }
}
