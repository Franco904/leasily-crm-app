package com.fstengineering.leasilycrm.occupiedPeriodRegistration.presentation

import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.application.services.interfaces.FileStorageService
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.presentation.models.LeadTenantUiState
import com.fstengineering.leasilycrm.core.presentation.models.PropertyCardUiState
import com.fstengineering.leasilycrm.core.presentation.utils.onApplicationError
import com.fstengineering.leasilycrm.core.presentation.utils.onDomainErrors
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toInlineMessage
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.commands.StayCommandModel
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.commands.UnavailablePeriodCommandModel
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.queries.SelectablePropertiesArgs
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.queries.StayArgs
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.queries.UnavailablePeriodArgs
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.repositories.PropertyQueryRepository
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.repositories.TenantQueryRepository
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.useCases.ReadSelectablePropertiesUseCase
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.useCases.ReadStayUseCase
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.useCases.ReadUnavailablePeriodUseCase
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.useCases.WriteStayUseCase
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.useCases.WriteUnavailablePeriodUseCase
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.presentation.mappers.toCommandModel
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.presentation.mappers.toUiState
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.presentation.models.OccupiedPeriodErrorUiState
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.presentation.models.OccupiedPeriodField
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.presentation.models.OccupiedPeriodFormUiState
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.presentation.models.StayFileUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class OccupiedPeriodRegistrationViewModel(
    private val readStay: ReadStayUseCase,
    private val readUnavailablePeriod: ReadUnavailablePeriodUseCase,
    private val readSelectableProperties: ReadSelectablePropertiesUseCase,
    private val writeStay: WriteStayUseCase,
    private val writeUnavailablePeriod: WriteUnavailablePeriodUseCase,
    private val propertyQueryRepository: PropertyQueryRepository,
    private val tenantQueryRepository: TenantQueryRepository,
    private val fileStorageService: FileStorageService,
) : ViewModel() {
    private var isInitialized = false

    private val _uiEvents = Channel<UiEvent>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _properties =
        MutableStateFlow<ImmutableList<PropertyCardUiState>>(persistentListOf())
    val properties = _properties.asStateFlow()

    private val _tenants = MutableStateFlow<ImmutableList<LeadTenantUiState>>(persistentListOf())
    val tenants = _tenants.asStateFlow()

    private val _draftFormUiState =
        MutableStateFlow<OccupiedPeriodFormUiState>(OccupiedPeriodFormUiState.Loading)
    val draftFormUiState = _draftFormUiState.asStateFlow()

    private var formToEdit: OccupiedPeriodFormUiState? = null

    private val _formErrors = MutableStateFlow(OccupiedPeriodErrorUiState())
    val formErrors = _formErrors.asStateFlow()

    private val _propertySearchInput = MutableStateFlow<String?>(null)
    val propertySearchInput = _propertySearchInput.asStateFlow()

    private val _tenantSearchInput = MutableStateFlow<String?>(null)
    val tenantSearchInput = _tenantSearchInput.asStateFlow()

    fun init(
        occupiedPeriodId: Long?,
        propertyId: Long?,
        tenantId: Long?,
        isUnavailablePeriod: Boolean,
    ) {
        if (isInitialized) return

        loadData(occupiedPeriodId, propertyId, tenantId, isUnavailablePeriod)

        isInitialized = true
    }

    private fun loadData(
        occupiedPeriodId: Long?,
        propertyId: Long?,
        tenantId: Long?,
        isUnavailablePeriod: Boolean,
    ) {
        viewModelScope.launch {
            if (isUnavailablePeriod) {
                readUnavailablePeriod(UnavailablePeriodArgs(id = occupiedPeriodId))
                    .onError { e -> _uiEvents.trySend(UiEvent.BottomSheetError(e)) }
                    .onSuccess { storedUnavailablePeriod ->
                        val draftForm = storedUnavailablePeriod.toUiState()

                        _draftFormUiState.update { draftForm }
                        formToEdit = occupiedPeriodId?.let { draftForm }
                    }
            } else {
                readStay(StayArgs(id = occupiedPeriodId))
                    .onError { e -> _uiEvents.trySend(UiEvent.BottomSheetError(e)) }
                    .onSuccess { storedStay ->
                        val draftForm = storedStay.toUiState()

                        _draftFormUiState.update { draftForm }
                        formToEdit = occupiedPeriodId?.let { draftForm }
                    }
            }

            if (occupiedPeriodId == null) {
                if (propertyId != null) getPropertyToAutofill(propertyId)
                if (tenantId != null) getTenantToAutofill(tenantId)
            }

            watchPropertySearchInput(occupiedPeriodId)
            watchTenantSearchInput()
        }
    }

    private fun getPropertyToAutofill(propertyId: Long) {
        viewModelScope.launch {
            propertyQueryRepository.getById(propertyId)
                .onError { e -> _uiEvents.trySend(UiEvent.BottomSheetError(e)) }
                .onSuccess { storedProperty ->
                    _draftFormUiState.update {
                        when (it) {
                            is OccupiedPeriodFormUiState.Loaded.Stay -> it.copy(property = storedProperty.toUiState())
                            is OccupiedPeriodFormUiState.Loaded.UnavailablePeriod -> it.copy(
                                property = storedProperty.toUiState()
                            )

                            else -> it
                        }
                    }
                }
        }
    }

    private fun getTenantToAutofill(tenantId: Long) {
        viewModelScope.launch {
            tenantQueryRepository.getById(tenantId)
                .onError { e -> _uiEvents.trySend(UiEvent.BottomSheetError(e)) }
                .onSuccess { storedTenant ->
                    _draftFormUiState.update {
                        when (it) {
                            is OccupiedPeriodFormUiState.Loaded.Stay -> it.copy(tenant = storedTenant.toUiState())
                            else -> it
                        }
                    }
                }
        }
    }

    @OptIn(FlowPreview::class)
    fun watchPropertySearchInput(stayId: Long?) {
        viewModelScope.launch {
            propertySearchInput
                .debounce(500.milliseconds)
                .collect { searchInput ->
                    val args = SelectablePropertiesArgs(
                        matching = searchInput,
                        stayId = stayId,
                    )

                    readSelectableProperties(args)
                        .onError { e -> _uiEvents.trySend(UiEvent.BottomSheetError(e)) }
                        .onSuccess { matchingProperties ->
                            val properties =
                                matchingProperties.map { it.toUiState() }.toPersistentList()
                            _properties.update { properties }
                        }
                }
        }
    }

    @OptIn(FlowPreview::class)
    fun watchTenantSearchInput() {
        viewModelScope.launch {
            tenantSearchInput
                .debounce(500.milliseconds)
                .collect { searchInput ->
                    val tenant = (formToEdit as? OccupiedPeriodFormUiState.Loaded.Stay)?.tenant

                    tenantQueryRepository.getAllWithoutUpcomingStays(
                        matching = searchInput,
                        currentTenantId = tenant?.id,
                    )
                        .onError { e -> _uiEvents.trySend(UiEvent.BottomSheetError(e)) }
                        .onSuccess { matchingTenants ->
                            val tenants =
                                matchingTenants.map { it.toUiState() }.toPersistentList()
                            _tenants.update { tenants }
                        }
                }
        }
    }

    fun onPropertySearchInputChange(searchInput: String?) {
        _propertySearchInput.update { searchInput?.ifBlank { null } }
    }

    fun onTenantSearchInputChange(searchInput: String?) {
        _tenantSearchInput.update { searchInput?.ifBlank { null } }
    }

    fun onOwnedStayFormChange(newForm: OccupiedPeriodFormUiState.Loaded.Stay) {
        val form = (draftFormUiState.value as? OccupiedPeriodFormUiState.Loaded.Stay) ?: return

        _formErrors.update {
            val newErrors = it.errors.mutate { map ->
                if (form.property != newForm.property) map[OccupiedPeriodField.Property] = null
                if (form.tenant != newForm.tenant) map[OccupiedPeriodField.Tenant] = null
                if (form.checkInDateTime != newForm.checkInDateTime) map[OccupiedPeriodField.Period] =
                    null
                if (form.checkInTime != newForm.checkInTime) map[OccupiedPeriodField.CheckInTime] =
                    null
                if (form.checkOutTime != newForm.checkOutTime) map[OccupiedPeriodField.CheckOutTime] =
                    null
                if (form.commission != newForm.commission) map[OccupiedPeriodField.Commission] =
                    null
            }

            it.copy(errors = newErrors)
        }

        _draftFormUiState.update { form }
    }

    fun onUnavailablePeriodFormChange(newForm: OccupiedPeriodFormUiState.Loaded.UnavailablePeriod) {
        val form =
            (draftFormUiState.value as? OccupiedPeriodFormUiState.Loaded.UnavailablePeriod)
                ?: return

        _formErrors.update {
            val newErrors = it.errors.mutate { map ->
                if (form.property != newForm.property) map[OccupiedPeriodField.Property] = null
                if (form.checkInDateTime != newForm.checkInDateTime) map[OccupiedPeriodField.Period] =
                    null
            }

            it.copy(errors = newErrors)
        }

        _draftFormUiState.update { form }
    }

    fun clearCommissionError() {
        _formErrors.update {
            val newErrors = it.errors.mutate { map -> map[OccupiedPeriodField.Commission] = null }
            it.copy(errors = newErrors)
        }
    }

    fun onStayFilePick(
        contentUriString: String,
        defaultFileName: String,
    ) {
        val form = (draftFormUiState.value as? OccupiedPeriodFormUiState.Loaded.Stay) ?: return

        viewModelScope.launch {
            val fileName = fileStorageService.getFileName(contentUriString)
                .fold(
                    onError = { e ->
                        _uiEvents.trySend(UiEvent.BottomSheetError(e))
                        return@launch
                    },
                    onSuccess = { fileName -> fileName },
                )

            val fileDraft = StayFileUiState(
                uri = contentUriString.toUri(),
                name = fileName ?: defaultFileName,
            )

            onOwnedStayFormChange(newForm = form.addFile(fileDraft))
        }
    }

    fun saveStay() {
        if (formToEdit != null && draftFormUiState.value == formToEdit) {
            _uiEvents.trySend(UiEvent.StayEditSuccess(messageId = R.string.no_data_changed_success_message))
            return
        }

        val command = when (val form = draftFormUiState.value) {
            is OccupiedPeriodFormUiState.Loaded.Stay -> form.toCommandModel()
            is OccupiedPeriodFormUiState.Loaded.UnavailablePeriod -> form.toCommandModel()
            else -> return
        }

        viewModelScope.launch {
            val editMessageId = when (command) {
                is StayCommandModel -> {
                    writeStay(command)
                        .onApplicationError {
                            _uiEvents.trySend(UiEvent.BottomSheetError(it))
                            return@launch
                        }
                        .onDomainErrors { errors ->
                            setFieldErrors(errors)
                            return@launch
                        }

                    R.string.edit_stay_success_message
                }

                is UnavailablePeriodCommandModel -> {
                    writeUnavailablePeriod(command)
                        .onApplicationError {
                            _uiEvents.trySend(UiEvent.BottomSheetError(it))
                            return@launch
                        }
                        .onDomainErrors { errors ->
                            setFieldErrors(errors)
                            return@launch
                        }

                    R.string.edit_unavailable_period_success_message
                }

                else -> return@launch
            }

            val saveEvent =
                if (formToEdit != null) UiEvent.StayEditSuccess(messageId = editMessageId)
                else UiEvent.StayCreateSuccess

            _uiEvents.trySend(saveEvent)
        }
    }

    private suspend fun setFieldErrors(errors: List<DomainError>) {
        _formErrors.update { OccupiedPeriodErrorUiState() }
        delay(150.milliseconds)

        val stayPropertyError = errors.firstOrNull { it is DomainError.StayProperty }
        val stayTenantError = errors.firstOrNull { it is DomainError.StayTenant }
        val periodError = errors.firstOrNull { it is DomainError.StayPeriod }
        val checkInTimeError = errors.firstOrNull { it is DomainError.StayCheckInTime }
        val checkOutTimeError = errors.firstOrNull { it is DomainError.StayCheckOutTime }
        val commissionError = errors.firstOrNull { it is DomainError.StayCommission }

        _formErrors.update {
            val newErrors = it.errors.mutate { map ->
                map[OccupiedPeriodField.Property] = stayPropertyError?.toInlineMessage()
                map[OccupiedPeriodField.Tenant] = stayTenantError?.toInlineMessage()
                map[OccupiedPeriodField.Period] = periodError?.toInlineMessage()
                map[OccupiedPeriodField.CheckInTime] = checkInTimeError?.toInlineMessage()
                map[OccupiedPeriodField.CheckOutTime] = checkOutTimeError?.toInlineMessage()
                map[OccupiedPeriodField.Commission] = commissionError?.toInlineMessage()
            }

            it.copy(errors = newErrors)
        }

        val firstFieldWithError =
            _formErrors.value.errors.entries.first { it.value != null }.key
        _uiEvents.trySend(UiEvent.ScrollToField(firstFieldWithError))
    }

    fun onExit() {
        val needConfirmation = when {
            formToEdit != null -> {
                formToEdit != draftFormUiState.value
            }

            else -> {
                val loadedForm = (draftFormUiState.value as? OccupiedPeriodFormUiState.Loaded)
                loadedForm?.isAnyFormFieldInitialized ?: false
            }
        }

        _uiEvents.trySend(UiEvent.Exit(needConfirmation))
    }

    override fun onCleared() {
        _uiEvents.close()

        super.onCleared()
    }

    sealed interface UiEvent {
        data class BottomSheetError(val error: ApplicationError) : UiEvent

        data class ScrollToField(val field: OccupiedPeriodField) : UiEvent

        data class StayEditSuccess(val messageId: Int) : UiEvent

        data object StayCreateSuccess : UiEvent

        data class Exit(val needConfirmation: Boolean) : UiEvent
    }
}
