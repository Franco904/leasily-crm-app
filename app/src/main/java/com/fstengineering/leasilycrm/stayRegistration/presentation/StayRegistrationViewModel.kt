package com.fstengineering.leasilycrm.stayRegistration.presentation

import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.application.services.interfaces.FileStorageService
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.helpers.AppDate
import com.fstengineering.leasilycrm.core.domain.helpers.AppDateTime
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.presentation.models.LeadTenantUiState
import com.fstengineering.leasilycrm.core.presentation.models.PropertyUiState
import com.fstengineering.leasilycrm.core.presentation.utils.onApplicationError
import com.fstengineering.leasilycrm.core.presentation.utils.onDomainErrors
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toInlineMessage
import com.fstengineering.leasilycrm.stayRegistration.application.models.StayCommandModel
import com.fstengineering.leasilycrm.stayRegistration.application.models.StayQueryModel
import com.fstengineering.leasilycrm.stayRegistration.application.models.UnavailablePeriodCommandModel
import com.fstengineering.leasilycrm.stayRegistration.application.models.UnavailablePeriodQueryModel
import com.fstengineering.leasilycrm.stayRegistration.application.repositories.PropertyQueryRepository
import com.fstengineering.leasilycrm.stayRegistration.application.repositories.TenantQueryRepository
import com.fstengineering.leasilycrm.stayRegistration.application.useCases.ReadStayUseCase
import com.fstengineering.leasilycrm.stayRegistration.application.useCases.ReadUnavailablePeriodUseCase
import com.fstengineering.leasilycrm.stayRegistration.application.useCases.WriteStayUseCase
import com.fstengineering.leasilycrm.stayRegistration.application.useCases.WriteUnavailablePeriodUseCase
import com.fstengineering.leasilycrm.stayRegistration.presentation.mappers.toCommandModel
import com.fstengineering.leasilycrm.stayRegistration.presentation.mappers.toUiState
import com.fstengineering.leasilycrm.stayRegistration.presentation.models.StayDraftFormUiState
import com.fstengineering.leasilycrm.stayRegistration.presentation.models.StayErrorUiState
import com.fstengineering.leasilycrm.stayRegistration.presentation.models.StayField
import com.fstengineering.leasilycrm.stayRegistration.presentation.models.StayFileUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentSet
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

class StayRegistrationViewModel(
    private val readStay: ReadStayUseCase,
    private val readUnavailablePeriod: ReadUnavailablePeriodUseCase,
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
        MutableStateFlow<ImmutableList<PropertyUiState>>(persistentListOf())
    val properties = _properties.asStateFlow()

    private val _tenants = MutableStateFlow<ImmutableList<LeadTenantUiState>>(persistentListOf())
    val tenants = _tenants.asStateFlow()

    private val _draftFormUiState =
        MutableStateFlow<StayDraftFormUiState>(StayDraftFormUiState.Loading)
    val draftFormUiState = _draftFormUiState.asStateFlow()

    private var formToEdit: StayDraftFormUiState? = null

    private val _formErrors = MutableStateFlow(StayErrorUiState())
    val formErrors = _formErrors.asStateFlow()

    private val _propertySearchInput = MutableStateFlow<String?>(null)
    val propertySearchInput = _propertySearchInput.asStateFlow()

    private val _tenantSearchInput = MutableStateFlow<String?>(null)
    val tenantSearchInput = _tenantSearchInput.asStateFlow()

    fun init(
        stayId: Long?,
        propertyId: Long?,
        tenantId: Long?,
        isUnavailablePeriod: Boolean,
    ) {
        if (isInitialized) return

        loadData(stayId, propertyId, tenantId, isUnavailablePeriod)

        watchPropertySearchInput()
        watchTenantSearchInput()

        isInitialized = true
    }

    private fun loadData(
        stayId: Long?,
        propertyId: Long?,
        tenantId: Long?,
        isUnavailablePeriod: Boolean,
    ) {
        viewModelScope.launch {
            if (isUnavailablePeriod) {
                val query = UnavailablePeriodQueryModel(id = stayId)

                readUnavailablePeriod(query)
                    .onError { e -> _uiEvents.trySend(UiEvent.BottomSheetError(e)) }
                    .onSuccess { storedUnavailablePeriod ->
                        val draftForm = storedUnavailablePeriod.toUiState()

                        _draftFormUiState.update { draftForm }
                        formToEdit = stayId?.let { draftForm }
                    }
            } else {
                val query = StayQueryModel(id = stayId)

                readStay(query)
                    .onError { e -> _uiEvents.trySend(UiEvent.BottomSheetError(e)) }
                    .onSuccess { storedStay ->
                        val draftForm = storedStay.toUiState()

                        _draftFormUiState.update { draftForm }
                        formToEdit = stayId?.let { draftForm }
                    }
            }

            if (stayId == null) {
                if (propertyId != null) getPropertyToAutofill(propertyId)
                if (tenantId != null) getTenantToAutofill(tenantId)
            }
        }
    }

    private fun getPropertyToAutofill(propertyId: Long) {
        viewModelScope.launch {
            propertyQueryRepository.getById(propertyId)
                .onError { e -> _uiEvents.trySend(UiEvent.BottomSheetError(e)) }
                .onSuccess { storedProperty ->
                    _draftFormUiState.update {
                        when (it) {
                            is StayDraftFormUiState.StayOwnedLoaded -> it.copy(property = storedProperty.toUiState())
                            is StayDraftFormUiState.UnavailablePeriodLoaded -> it.copy(property = storedProperty.toUiState())
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
                            is StayDraftFormUiState.StayOwnedLoaded -> it.copy(tenant = storedTenant.toUiState())
                            else -> it
                        }
                    }
                }
        }
    }

    @OptIn(FlowPreview::class)
    fun watchPropertySearchInput() {
        viewModelScope.launch {
            propertySearchInput
                .debounce(500.milliseconds)
                .collect { searchInput ->
                    propertyQueryRepository.getAllMatching(matching = searchInput)
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
                    val stayOwnedForm = (formToEdit as? StayDraftFormUiState.StayOwnedLoaded)

                    tenantQueryRepository.getAllWithoutUpcomingStays(
                        matching = searchInput,
                        currentTenantId = stayOwnedForm?.tenant?.id,
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

    fun onStayOwnedFormChange(newForm: StayDraftFormUiState.StayOwnedLoaded) {
        val form = (draftFormUiState.value as? StayDraftFormUiState.StayOwnedLoaded) ?: return

        _formErrors.update {
            val newErrors = it.errors.mutate { map ->
                if (form.property != newForm.property) map[StayField.Property] = null
                if (form.tenant != newForm.tenant) map[StayField.Tenant] = null
                if (form.checkInDateTime != newForm.checkInDateTime) map[StayField.Period] = null
                if (form.checkInTime != newForm.checkInTime) map[StayField.CheckInTime] = null
                if (form.checkOutTime != newForm.checkOutTime) map[StayField.CheckOutTime] = null
                if (form.commission != newForm.commission) map[StayField.Commission] = null
            }

            it.copy(errors = newErrors)
        }

        _draftFormUiState.update { form }
    }

    fun onUnavailablePeriodFormChange(newForm: StayDraftFormUiState.UnavailablePeriodLoaded) {
        val form =
            (draftFormUiState.value as? StayDraftFormUiState.UnavailablePeriodLoaded) ?: return

        _formErrors.update {
            val newErrors = it.errors.mutate { map ->
                if (form.property != newForm.property) map[StayField.Property] = null
                if (form.checkInDateTime != newForm.checkInDateTime) map[StayField.Period] = null
            }

            it.copy(errors = newErrors)
        }

        _draftFormUiState.update { form }
    }

    fun clearCommissionError() {
        _formErrors.update {
            val newErrors = it.errors.mutate { map -> map[StayField.Commission] = null }
            it.copy(errors = newErrors)
        }
    }

    fun getPropertyAlreadySelectedDatesForStay(
        propertyStaysPeriods: ImmutableList<Pair<AppDateTime, AppDateTime>>,
    ): ImmutableSet<AppDate> {
        val isEditing = formToEdit != null

        val formCheckInDateTime = when (val form = draftFormUiState.value) {
            is StayDraftFormUiState.StayOwnedLoaded -> form.checkInDateTime
            is StayDraftFormUiState.UnavailablePeriodLoaded -> form.checkInDateTime
            else -> null
        }

        val sortedStaysPeriods = propertyStaysPeriods.sortedBy { it.first }

        val propertyAlreadySelectedDatesForStay = sortedStaysPeriods
            .asSequence()
            .filter { (checkInDateTime, _) ->
                val isSelectedDate =
                    checkInDateTime.dayOfMonth == formCheckInDateTime?.dayOfMonth &&
                            checkInDateTime.month == formCheckInDateTime.month &&
                            checkInDateTime.year == formCheckInDateTime.year

                !isEditing || !isSelectedDate
            }
            .flatMap { (checkInDateTime, checkOutDateTime) ->
                val isCheckInAlsoACheckOut = sortedStaysPeriods.any { (_, anyCheckOutDateTime) ->
                    checkInDateTime.toDate() == anyCheckOutDateTime.toDate()
                }

                val isCheckOutAlsoACheckIn = sortedStaysPeriods.any { (anyCheckInDateTime, _) ->
                    checkOutDateTime.toDate() == anyCheckInDateTime.toDate()
                }

                val startDateTime =
                    if (isCheckInAlsoACheckOut) checkInDateTime
                    else checkInDateTime.plusDays(1)

                val endDateTime =
                    if (isCheckOutAlsoACheckIn) checkOutDateTime
                    else checkOutDateTime.minusDays(1)

                generateSequence(startDateTime) { it.plusDays(1) }
                    .takeWhile { it <= endDateTime }
                    .map { it.toDate() }
            }
            .toPersistentSet()

        return propertyAlreadySelectedDatesForStay
    }

    fun onStayFilePick(
        contentUriString: String,
        defaultFileName: String,
    ) {
        val form = (draftFormUiState.value as? StayDraftFormUiState.StayOwnedLoaded) ?: return

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

            onStayOwnedFormChange(newForm = form.addFile(fileDraft))
        }
    }

    fun saveStay() {
        if (formToEdit != null && draftFormUiState.value == formToEdit) {
            _uiEvents.trySend(UiEvent.StayEditSuccess(messageId = R.string.no_data_changed_success_message))
            return
        }

        val command = when (val form = draftFormUiState.value) {
            is StayDraftFormUiState.StayOwnedLoaded -> form.toCommandModel()
            is StayDraftFormUiState.UnavailablePeriodLoaded -> form.toCommandModel()
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
        _formErrors.update { StayErrorUiState() }
        delay(150.milliseconds)

        val stayPropertyError = errors.firstOrNull { it is DomainError.StayProperty }
        val stayTenantError = errors.firstOrNull { it is DomainError.StayTenant }
        val periodError = errors.firstOrNull { it is DomainError.StayPeriod }
        val checkInTimeError = errors.firstOrNull { it is DomainError.StayCheckInTime }
        val checkOutTimeError = errors.firstOrNull { it is DomainError.StayCheckOutTime }
        val commissionError = errors.firstOrNull { it is DomainError.StayCommission }

        _formErrors.update {
            val newErrors = it.errors.mutate { map ->
                map[StayField.Property] = stayPropertyError?.toInlineMessage()
                map[StayField.Tenant] = stayTenantError?.toInlineMessage()
                map[StayField.Period] = periodError?.toInlineMessage()
                map[StayField.CheckInTime] = checkInTimeError?.toInlineMessage()
                map[StayField.CheckOutTime] = checkOutTimeError?.toInlineMessage()
                map[StayField.Commission] = commissionError?.toInlineMessage()
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

            else -> when (val form = draftFormUiState.value) {
                is StayDraftFormUiState.StayOwnedLoaded -> form.isAnyFormFieldInitialized
                is StayDraftFormUiState.UnavailablePeriodLoaded -> form.isAnyFormFieldInitialized
                else -> false
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

        data class ScrollToField(val field: StayField) : UiEvent

        data class StayEditSuccess(val messageId: Int) : UiEvent

        data object StayCreateSuccess : UiEvent

        data class Exit(val needConfirmation: Boolean) : UiEvent
    }
}
