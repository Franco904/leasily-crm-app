package com.fstengineering.leasilycrm.leadTenantRegistration.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.CommandUseCase
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.presentation.utils.onApplicationError
import com.fstengineering.leasilycrm.core.presentation.utils.onDomainErrors
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toInlineMessage
import com.fstengineering.leasilycrm.leadTenantRegistration.application.models.commands.LeadTenantCommandModel
import com.fstengineering.leasilycrm.leadTenantRegistration.application.repositories.LeadTenantQueryRepository
import com.fstengineering.leasilycrm.leadTenantRegistration.presentation.mappers.toCommandModel
import com.fstengineering.leasilycrm.leadTenantRegistration.presentation.mappers.toUiState
import com.fstengineering.leasilycrm.leadTenantRegistration.presentation.models.LeadTenantDraftFormUiState
import com.fstengineering.leasilycrm.leadTenantRegistration.presentation.models.LeadTenantErrorUiState
import com.fstengineering.leasilycrm.leadTenantRegistration.presentation.models.LeadTenantField
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
    private val writeLeadTenant: CommandUseCase<LeadTenantCommandModel>,
    private val leadTenantQueryRepository: LeadTenantQueryRepository,
) : ViewModel() {
    private var isInitialized = false

    private val _uiEvents = Channel<UiEvent>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _draftFormUiState =
        MutableStateFlow<LeadTenantDraftFormUiState>(LeadTenantDraftFormUiState.Loading)
    val draftFormUiState = _draftFormUiState.asStateFlow()

    private var formToEdit: LeadTenantDraftFormUiState? = null

    private val _formErrors = MutableStateFlow(LeadTenantErrorUiState())
    val formErrors = _formErrors.asStateFlow()

    private val _pastTags =
        MutableStateFlow<ImmutableList<String>>(persistentListOf())
    val pastTags = _pastTags.asStateFlow()

    private val _pastCities = MutableStateFlow<ImmutableList<String>>(persistentListOf())
    val pastCities = _pastCities.asStateFlow()

    private val _pastContactOrigins =
        MutableStateFlow<ImmutableList<String>>(persistentListOf())
    val pastContactOrigins = _pastContactOrigins.asStateFlow()

    fun init(leadTenantId: Long?) {
        if (isInitialized) return

        if (leadTenantId != null) {
            getLeadTenantToEdit(leadTenantId)
        }

        getPastTags()
        getPastCities()
        getPastContactOrigins()

        isInitialized = true
    }

    private fun getLeadTenantToEdit(leadTenantId: Long) {
        viewModelScope.launch {
            leadTenantQueryRepository.getById(leadTenantId)
                .onError { e -> _uiEvents.trySend(UiEvent.BottomSheetError(e)) }
                .onSuccess { storedLeadTenant ->
                    val form = storedLeadTenant.toUiState()

                    _draftFormUiState.update { form }
                    formToEdit = form
                }
        }
    }

    private fun getPastTags() {
        viewModelScope.launch {
            leadTenantQueryRepository.getAllTagNames()
                .onError { e -> _uiEvents.trySend(UiEvent.BottomSheetError(e)) }
                .onSuccess { tags -> _pastTags.update { tags } }
        }
    }

    private fun getPastCities() {
        viewModelScope.launch {
            leadTenantQueryRepository.getAllCityNames()
                .onError { e -> _uiEvents.trySend(UiEvent.BottomSheetError(e)) }
                .onSuccess { cities -> _pastCities.update { cities } }
        }
    }

    private fun getPastContactOrigins() {
        viewModelScope.launch {
            leadTenantQueryRepository.getAllContactOrigins()
                .onError { e -> _uiEvents.trySend(UiEvent.BottomSheetError(e)) }
                .onSuccess { contactOriginTypes -> _pastContactOrigins.update { contactOriginTypes } }
        }
    }

    fun onLeadTenantFormChange(newForm: LeadTenantDraftFormUiState.Loaded) {
        val form = (draftFormUiState.value as? LeadTenantDraftFormUiState.Loaded) ?: return

        _formErrors.update {
            val newErrors = it.errors.mutate { map ->
                if (form.name != newForm.name) map[LeadTenantField.Name] = null
                if (form.phone != newForm.phone) map[LeadTenantField.Phone] = null
                if (form.email != newForm.email) map[LeadTenantField.Email] = null
            }

            it.copy(errors = newErrors)
        }

        _draftFormUiState.update { newForm }
    }

    fun saveLeadTenant() {
        val form = (draftFormUiState.value as? LeadTenantDraftFormUiState.Loaded) ?: return

        if (formToEdit != null && form == formToEdit) {
            _uiEvents.trySend(UiEvent.LeadTenantEditSuccess(messageId = R.string.no_data_changed_success_message))
            return
        }

        viewModelScope.launch {
            writeLeadTenant(form.toCommandModel())
                .onApplicationError {
                    _uiEvents.trySend(UiEvent.BottomSheetError(it))
                    return@launch
                }
                .onDomainErrors { errors ->
                    setFieldErrors(errors)
                    return@launch
                }

            val messageId =
                if (form.isLead) R.string.edit_lead_success_message else R.string.edit_tenant_success_message

            val saveEvent =
                if (formToEdit != null) UiEvent.LeadTenantEditSuccess(messageId)
                else UiEvent.LeadCreateSuccess

            _uiEvents.trySend(saveEvent)
        }
    }

    private suspend fun setFieldErrors(errors: List<DomainError>) {
        _formErrors.update { LeadTenantErrorUiState() }
        delay(150.milliseconds)

        val nameError = errors.firstOrNull { it is DomainError.Name }
        val phoneError = errors.firstOrNull { it is DomainError.Phone }
        val emailError = errors.firstOrNull { it is DomainError.Email }

        _formErrors.update {
            val newErrors = it.errors.mutate { map ->
                map[LeadTenantField.Name] = nameError?.toInlineMessage()
                map[LeadTenantField.Phone] = phoneError?.toInlineMessage()
                map[LeadTenantField.Email] = emailError?.toInlineMessage()
            }

            it.copy(errors = newErrors)
        }

        val firstFieldWithError = _formErrors.value.errors.entries.first { it.value != null }.key
        _uiEvents.trySend(UiEvent.ScrollToField(firstFieldWithError))
    }

    fun onExit() {
        val form = (draftFormUiState.value as? LeadTenantDraftFormUiState.Loaded) ?: return

        val needConfirmation = when {
            formToEdit != null -> formToEdit != form
            else -> form.isAnyFormFieldInitialized
        }

        _uiEvents.trySend(UiEvent.Exit(needConfirmation))
    }

    override fun onCleared() {
        _uiEvents.close()

        super.onCleared()
    }

    sealed interface UiEvent {
        data class BottomSheetError(val error: ApplicationError) : UiEvent

        data class ScrollToField(val field: LeadTenantField) : UiEvent

        data object LeadCreateSuccess : UiEvent

        data class LeadTenantEditSuccess(val messageId: Int) : UiEvent

        data class Exit(val needConfirmation: Boolean) : UiEvent
    }
}
