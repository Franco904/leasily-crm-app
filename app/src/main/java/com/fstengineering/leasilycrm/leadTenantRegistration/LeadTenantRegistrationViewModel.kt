package com.fstengineering.leasilycrm.leadTenantRegistration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.models.CustomerSegment
import com.fstengineering.leasilycrm.core.domain.models.Tenant
import com.fstengineering.leasilycrm.core.domain.models.TenantContactOriginType
import com.fstengineering.leasilycrm.core.domain.repositories.CustomerSegmentRepository
import com.fstengineering.leasilycrm.core.domain.repositories.TenantContactOriginTypeRepository
import com.fstengineering.leasilycrm.core.domain.repositories.TenantRepository
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onErrors
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.domain.validators.interfaces.TenantValidator
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toInlineMessage
import com.fstengineering.leasilycrm.leadTenantRegistration.models.LeadTenantErrorUiState
import com.fstengineering.leasilycrm.leadTenantRegistration.models.LeadTenantField
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class LeadTenantRegistrationViewModel(
    private val tenantRepository: TenantRepository,
    private val customerSegmentRepository: CustomerSegmentRepository,
    private val tenantContactOriginTypeRepository: TenantContactOriginTypeRepository,
    private val tenantValidator: TenantValidator,
) : ViewModel() {
    private var isInitialized = false

    private val _uiEvents = Channel<UiEvents>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _leadTenantForm = MutableStateFlow(Tenant())
    val leadTenantForm = _leadTenantForm.asStateFlow()

    var leadTenantToEdit: Tenant? = null
        private set

    private val _formErrors = MutableStateFlow(LeadTenantErrorUiState())
    val formErrors = _formErrors.asStateFlow()

    private val _tenantSegments =
        MutableStateFlow<ImmutableList<CustomerSegment>>(persistentListOf())
    val tenantSegments = _tenantSegments.asStateFlow()

    private val _pastCities = MutableStateFlow<ImmutableList<String>>(persistentListOf())
    val pastCities = _pastCities.asStateFlow()

    private val _tenantContactOriginTypes =
        MutableStateFlow<ImmutableList<TenantContactOriginType>>(persistentListOf())
    val tenantContactOriginTypes = _tenantContactOriginTypes.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun init(leadTenantId: Long?) {
        if (isInitialized) return

        _isLoading.update { true }

        if (leadTenantId != null) getLeadTenantToEdit(leadTenantId)

        getTenantSegments()
        getPastCities()
        getTenantContactOriginTypes()

        isInitialized = true
    }

    private fun getLeadTenantToEdit(leadTenantId: Long) {
        viewModelScope.launch {
            tenantRepository.getById(leadTenantId)
                .onError { e -> _uiEvents.send(UiEvents.Error(e)) }
                .onSuccess { storedLeadTenant ->
                    _leadTenantForm.update { storedLeadTenant }
                    leadTenantToEdit = storedLeadTenant
                }
        }
    }

    private fun getTenantSegments() {
        viewModelScope.launch {
            customerSegmentRepository.getAll()
                .onError { e -> _uiEvents.send(UiEvents.Error(e)) }
                .onSuccess { storedCustomerSegments ->
                    _tenantSegments.update { storedCustomerSegments }
                }
        }
    }

    private fun getPastCities() {
        viewModelScope.launch {
            tenantRepository.getAllCityNames()
                .onError { e -> _uiEvents.send(UiEvents.Error(e)) }
                .onSuccess { storedCities ->
                    _pastCities.update { storedCities }
                }
        }
    }

    private fun getTenantContactOriginTypes() {
        viewModelScope.launch {
            tenantContactOriginTypeRepository.getAll()
                .onError { e -> _uiEvents.send(UiEvents.Error(e)) }
                .onSuccess { storedTenantContactOriginTypes ->
                    _tenantContactOriginTypes.update { storedTenantContactOriginTypes }
                }

            _isLoading.update { false }
        }
    }

    fun onLeadTenantFormChange(form: Tenant) {
        _formErrors.update {
            val newErrors = it.errors.mutate { map ->
                if (leadTenantForm.value.name != form.name) {
                    map[LeadTenantField.Name] = null
                }
                if (leadTenantForm.value.phone != form.phone) {
                    map[LeadTenantField.Phone] = null
                }
                if (leadTenantForm.value.email != form.email) {
                    map[LeadTenantField.Email] = null
                }
            }

            it.copy(errors = newErrors)
        }

        _leadTenantForm.update { form }
    }

    fun saveLeadTenant() {
        viewModelScope.launch {
            val leadTenant = leadTenantForm.value

            if (leadTenantToEdit != null && leadTenant == leadTenantToEdit) {
                _uiEvents.send(UiEvents.LeadTenantEditSuccess(messageId = R.string.no_data_changed_success_message))
                return@launch
            }

            val isThereStoredTenantWithSameName = leadTenant.name?.let {
                tenantRepository.isThereTenantWithName(
                    tenantName = leadTenant.name,
                    currentStoredTenantName = leadTenantToEdit?.name,
                )
                    .fold(onError = { false }, onSuccess = { it })
            } ?: false

            tenantValidator.validate(
                tenant = leadTenant,
                isThereStoredTenantWithSameName = isThereStoredTenantWithSameName,
            )
                .onErrors { errors ->
                    setFieldErrors(errors)
                    return@launch
                }

            tenantRepository.insertOrUpdate(leadTenant)
                .onError { e -> return@launch _uiEvents.send(UiEvents.Error(e)) }

            val saveEvent =
                if (leadTenantToEdit != null) {
                    UiEvents.LeadTenantEditSuccess(
                        messageId = if (leadTenant.isLead) {
                            R.string.edit_lead_success_message
                        } else R.string.edit_tenant_success_message,
                    )
                } else UiEvents.LeadCreateSuccess

            _uiEvents.send(saveEvent)
        }
    }

    private suspend fun setFieldErrors(errors: List<DomainError>) {
        _formErrors.update { LeadTenantErrorUiState() }
        delay(150.milliseconds)

        val nameError = errors.firstOrNull { it is DomainError.TenantName }
        val phoneError = errors.firstOrNull { it is DomainError.TenantPhone }
        val emailError = errors.firstOrNull { it is DomainError.TenantEmail }

        _formErrors.update {
            val newErrors = it.errors.mutate { map ->
                map[LeadTenantField.Name] = nameError?.toInlineMessage()
                map[LeadTenantField.Phone] = phoneError?.toInlineMessage()
                map[LeadTenantField.Email] = emailError?.toInlineMessage()
            }

            it.copy(errors = newErrors)
        }

        val firstFieldWithError = _formErrors.value.errors.entries.first { it.value != null }.key
        _uiEvents.send(UiEvents.ScrollToField(firstFieldWithError))
    }

    sealed interface UiEvents {
        data class Error(val error: DataError) : UiEvents

        data class ScrollToField(val field: LeadTenantField) : UiEvents

        data object LeadCreateSuccess : UiEvents

        data class LeadTenantEditSuccess(val messageId: Int) : UiEvents
    }
}
