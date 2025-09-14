package com.fstengineering.leasilycrm.leadTenantRegistration

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.models.CustomerSegment
import com.fstengineering.leasilycrm.core.domain.models.Tenant
import com.fstengineering.leasilycrm.core.domain.models.TenantContactOriginType
import com.fstengineering.leasilycrm.core.domain.repositories.CustomerSegmentRepository
import com.fstengineering.leasilycrm.core.domain.repositories.TenantContactOriginTypeRepository
import com.fstengineering.leasilycrm.core.domain.repositories.TenantRepository
import com.fstengineering.leasilycrm.core.domain.repositories.UserRepository
import com.fstengineering.leasilycrm.core.domain.utils.ValidationError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onErrors
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.domain.validators.interfaces.TenantValidator
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toUiMessage
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
    private val userRepository: UserRepository,
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

    private val _tenantContactOriginTypes =
        MutableStateFlow<ImmutableList<TenantContactOriginType>>(persistentListOf())
    val tenantContactOriginTypes = _tenantContactOriginTypes.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun init(leadTenantId: Long?) {
        if (isInitialized) return

        _isLoading.update { true }

        watchSessionExpiration()
        if (leadTenantId != null) getLeadTenantToEdit(leadTenantId)

        getTenantSegments()
        getTenantContactOriginTypes()

        isInitialized = true
    }

    private fun watchSessionExpiration() {
        viewModelScope.launch {
            userRepository.watchSessionExpiration().collect { result ->
                result
                    .onError { error ->
                        _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                    }
                    .onSuccess { hasSessionExpired ->
                        if (hasSessionExpired) {
                            userRepository.deleteCachedUser()
                            _uiEvents.send(UiEvents.RedirectToSignIn)
                        }
                    }
            }
        }
    }

    private fun getLeadTenantToEdit(leadTenantId: Long) {
        viewModelScope.launch {
            tenantRepository.getById(leadTenantId)
                .onError { error ->
                    _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                }
                .onSuccess { storedLeadTenant ->
                    _leadTenantForm.update { storedLeadTenant }
                    leadTenantToEdit = storedLeadTenant
                }
        }
    }

    private fun getTenantSegments() {
        viewModelScope.launch {
            customerSegmentRepository.getAllTenantType()
                .onError { error ->
                    _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                }
                .onSuccess { storedCustomerSegments ->
                    _tenantSegments.update { storedCustomerSegments }
                }

            _isLoading.update { false }
        }
    }

    private fun getTenantContactOriginTypes() {
        viewModelScope.launch {
            tenantContactOriginTypeRepository.getAll()
                .onError { error ->
                    _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                }
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
                _uiEvents.send(UiEvents.LeadTenantEditSuccess(messageId = R.string.snackbar_no_data_changed))
                return@launch
            }

            tenantValidator.validate(tenant = leadTenant)
                .onErrors { errors ->
                    setFieldErrors(errors)
                    return@launch
                }

            tenantRepository.insertOrUpdate(leadTenant)
                .onError { error ->
                    _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                    return@launch
                }

            _uiEvents.send(
                if (leadTenantToEdit != null) {
                    UiEvents.LeadTenantEditSuccess(
                        messageId = if (leadTenant.isLead) {
                            R.string.snackbar_edit_lead_succeeded
                        } else R.string.snackbar_edit_tenant_succeeded,
                    )
                } else UiEvents.LeadCreateSuccess,
            )
        }
    }

    private suspend fun setFieldErrors(errors: List<ValidationError>) {
        _formErrors.update { LeadTenantErrorUiState() }
        delay(150.milliseconds)

        val nameError = errors.firstOrNull { it is ValidationError.TenantName }
        val phoneError = errors.firstOrNull { it is ValidationError.TenantPhone }
        val emailError = errors.firstOrNull { it is ValidationError.TenantEmail }

        _formErrors.update {
            val newErrors = it.errors.mutate { map ->
                map[LeadTenantField.Name] = nameError?.toUiMessage()
                map[LeadTenantField.Phone] = phoneError?.toUiMessage()
                map[LeadTenantField.Email] = emailError?.toUiMessage()
            }

            it.copy(errors = newErrors)
        }

        val firstFieldWithError = _formErrors.value.errors.entries.first { it.value != null }.key
        _uiEvents.send(UiEvents.ScrollToField(firstFieldWithError))
    }

    sealed interface UiEvents {
        data class DataSourceError(@StringRes val messageId: Int) : UiEvents

        data object RedirectToSignIn : UiEvents

        data class ScrollToField(val field: LeadTenantField) : UiEvents

        data object LeadCreateSuccess : UiEvents

        data class LeadTenantEditSuccess(@StringRes val messageId: Int) : UiEvents
    }
}
