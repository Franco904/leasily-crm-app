package com.fstengineering.leasilycrm.leadTenantRegistration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.entities.Tenant
import com.fstengineering.leasilycrm.core.domain.repositories.TenantRepository
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onErrors
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
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
    private val tenantContactOriginTypeRepository: TenantContactOriginTypeRepository,
    private val tenantValidator: TenantValidator,
) : ViewModel() {
    private var isInitialized = false

    private val _uiEvents = Channel<UiEvents>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _leadTenantForm = MutableStateFlow(Tenant())
    val leadTenantForm = _leadTenantForm.asStateFlow()

    private var leadTenantToEdit: Tenant? = null

    private val _formErrors = MutableStateFlow(LeadTenantErrorUiState())
    val formErrors = _formErrors.asStateFlow()

    private val _pastTags =
        MutableStateFlow<ImmutableList<String>>(persistentListOf())
    val pastTags = _pastTags.asStateFlow()

    private val _pastCities = MutableStateFlow<ImmutableList<String>>(persistentListOf())
    val pastCities = _pastCities.asStateFlow()

    private val _pastContactOriginTypes =
        MutableStateFlow<ImmutableList<TenantContactOriginType>>(persistentListOf())
    val pastContactOriginTypes = _pastContactOriginTypes.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun init(leadTenantId: Long?) {
        if (isInitialized) return

        if (leadTenantId != null) {
            _isLoading.update { true }

            getLeadTenantToEdit(leadTenantId)
        }

        getPastTags()
        getPastCities()
        getPastContactOriginTypes()

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

            _isLoading.update { false }
        }
    }

    private fun getPastTags() {
        viewModelScope.launch {
            tenantRepository.getAllTagNames()
                .onError { e -> _uiEvents.send(UiEvents.Error(e)) }
                .onSuccess { tags -> _pastTags.update { tags } }
        }
    }

    private fun getPastCities() {
        viewModelScope.launch {
            tenantRepository.getAllCityNames()
                .onError { e -> _uiEvents.send(UiEvents.Error(e)) }
                .onSuccess { cities -> _pastCities.update { cities } }
        }
    }

    private fun getPastContactOriginTypes() {
        viewModelScope.launch {
            tenantContactOriginTypeRepository.getAll()
                .onError { e -> _uiEvents.send(UiEvents.Error(e)) }
                .onSuccess { contactOriginTypes -> _pastContactOriginTypes.update { contactOriginTypes } }
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

            if (leadTenantToEdit != null && leadTenantToEdit == leadTenant) {
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

    fun onExit() {
        val needConfirmation = when {
            leadTenantToEdit != null -> leadTenantToEdit != leadTenantForm.value
            else -> leadTenantForm.value.isAnyFormFieldInitialized
        }

        viewModelScope.launch {
            _uiEvents.send(UiEvents.Exit(needConfirmation))
        }
    }

    sealed interface UiEvents {
        data class Error(val error: DataError) : UiEvents

        data class ScrollToField(val field: LeadTenantField) : UiEvents

        data object LeadCreateSuccess : UiEvents

        data class LeadTenantEditSuccess(val messageId: Int) : UiEvents

        data class Exit(val needConfirmation: Boolean) : UiEvents
    }
}
