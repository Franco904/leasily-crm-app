package com.fstengineering.leasilycrm.stayRegistration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDate
import com.fstengineering.leasilycrm.core.domain.models.Property
import com.fstengineering.leasilycrm.core.domain.models.Stay
import com.fstengineering.leasilycrm.core.domain.models.StayFile
import com.fstengineering.leasilycrm.core.domain.models.StayTransfer
import com.fstengineering.leasilycrm.core.domain.models.Tenant
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyRepository
import com.fstengineering.leasilycrm.core.domain.repositories.StayFileRepository
import com.fstengineering.leasilycrm.core.domain.repositories.StayRepository
import com.fstengineering.leasilycrm.core.domain.repositories.TenantRepository
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onErrors
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.domain.validators.interfaces.StayTransferValidator
import com.fstengineering.leasilycrm.core.domain.validators.interfaces.StayValidator
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toInlineMessage
import com.fstengineering.leasilycrm.stayRegistration.models.StayErrorUiState
import com.fstengineering.leasilycrm.stayRegistration.models.StayField
import com.fstengineering.leasilycrm.stayRegistration.models.StayTransferErrorUiState
import com.fstengineering.leasilycrm.stayRegistration.models.StayTransferField
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
    private val stayRepository: StayRepository,
    private val propertyRepository: PropertyRepository,
    private val tenantRepository: TenantRepository,
    private val stayFileRepository: StayFileRepository,
    private val stayValidator: StayValidator,
    private val stayTransferValidator: StayTransferValidator,
) : ViewModel() {
    private var isInitialized = false

    private val _uiEvents = Channel<UiEvents>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _stayForm = MutableStateFlow(Stay())
    val stayForm = _stayForm.asStateFlow()

    private val _transferForm = MutableStateFlow<StayTransfer?>(null)
    val transferForm = _transferForm.asStateFlow()

    private var stayToEdit: Stay? = null
    private var transferFormBeforeEdit: StayTransfer? = null

    private val _stayFormErrors = MutableStateFlow(StayErrorUiState())
    val stayFormErrors = _stayFormErrors.asStateFlow()

    private val _transferFormErrors = MutableStateFlow(StayTransferErrorUiState())
    val transferFormErrors = _transferFormErrors.asStateFlow()

    private val _properties = MutableStateFlow<ImmutableList<Property>>(persistentListOf())
    val properties = _properties.asStateFlow()

    private val _tenants = MutableStateFlow<ImmutableList<Tenant>>(persistentListOf())
    val tenants = _tenants.asStateFlow()

    private val _propertySearchInput = MutableStateFlow<String?>(null)
    val propertySearchInput = _propertySearchInput.asStateFlow()

    private val _tenantSearchInput = MutableStateFlow<String?>(null)
    val tenantSearchInput = _tenantSearchInput.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun init(
        stayId: Long?,
        propertyId: Long?,
        tenantId: Long?,
        isUnavailablePeriod: Boolean,
    ) {
        if (isInitialized) return

        _isLoading.update { true }

        if (stayId != null) getStayToEdit(stayId)

        if (stayId == null && propertyId != null) getPropertyToAutofill(propertyId)
        if (stayId == null && tenantId != null) getTenantToAutofill(tenantId)

        _stayForm.update { it.copy(isUnavailablePeriod = isUnavailablePeriod) }

        watchPropertySearchInput()
        watchTenantSearchInput()

        isInitialized = true
    }

    private fun getStayToEdit(stayId: Long) {
        viewModelScope.launch {
            stayRepository.getById(stayId)
                .onError { e -> _uiEvents.send(UiEvents.Error(e)) }
                .onSuccess { storedStay ->
                    _stayForm.update { storedStay }
                    stayToEdit = storedStay
                }
        }
    }

    private fun getPropertyToAutofill(propertyId: Long) {
        viewModelScope.launch {
            propertyRepository.getById(propertyId)
                .onError { e -> _uiEvents.send(UiEvents.Error(e)) }
                .onSuccess { storedProperty ->
                    _stayForm.update { it.copy(property = storedProperty) }
                }
        }
    }

    private fun getTenantToAutofill(tenantId: Long) {
        viewModelScope.launch {
            tenantRepository.getById(tenantId)
                .onError { e -> _uiEvents.send(UiEvents.Error(e)) }
                .onSuccess { storedTenant ->
                    _stayForm.update { it.copy(tenant = storedTenant) }
                }
        }
    }

    @OptIn(FlowPreview::class)
    fun watchPropertySearchInput() {
        viewModelScope.launch {
            propertySearchInput
                .debounce(500.milliseconds)
                .collect { searchInput ->
                    _isLoading.update { true }

                    propertyRepository.getAllMatching(matching = searchInput)
                        .onError { e -> _uiEvents.send(UiEvents.Error(e)) }
                        .onSuccess { matchingProperties ->
                            _properties.update { matchingProperties }
                            _isLoading.update { false }
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
                    _isLoading.update { true }

                    tenantRepository.getAllWithoutUpcomingStaysMatching(
                        matching = searchInput,
                        currentTenantId = stayToEdit?.tenant?.id,
                    )
                        .onError { e -> _uiEvents.send(UiEvents.Error(e)) }
                        .onSuccess { matchingTenants ->
                            _tenants.update { matchingTenants }
                            _isLoading.update { false }
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

    fun onStayFormChange(form: Stay) {
        _stayFormErrors.update {
            val newErrors = it.errors.mutate { map ->
                if (stayForm.value.property != form.property) {
                    map[StayField.Property] = null
                }
                if (stayForm.value.tenant != form.tenant) {
                    map[StayField.Tenant] = null
                }
                if (stayForm.value.checkInDateTime != form.checkInDateTime) {
                    map[StayField.Period] = null
                }
                if (stayForm.value.checkInHourMinute != form.checkInHourMinute) {
                    map[StayField.CheckInTime] = null
                }
                if (stayForm.value.checkOutHourMinute != form.checkOutHourMinute) {
                    map[StayField.CheckOutTime] = null
                }
            }

            it.copy(errors = newErrors)
        }

        _stayForm.update { form }
    }

    fun clearCommissionError() {
        _stayFormErrors.update {
            val newErrors = it.errors.mutate { map -> map[StayField.Commission] = null }
            it.copy(errors = newErrors)
        }
    }

    fun getPropertyAlreadySelectedDatesForStay(
        propertyStays: ImmutableList<Stay>,
    ): ImmutableSet<AppDate> {
        val isEditing = stayToEdit != null

        val sortedPropertyStays = propertyStays
            .sortedBy { it.checkInDateTime?.dateTime() }

        val propertyAlreadySelectedDatesForStay = sortedPropertyStays
            .asSequence()
            .filter {
                if (it.isDeleted) return@filter false

                val isSelectedDate =
                    it.checkInDateTime?.dayOfMonth == stayForm.value.checkInDateTime?.dayOfMonth &&
                            it.checkInDateTime?.month == stayForm.value.checkInDateTime?.month &&
                            it.checkInDateTime?.year == stayForm.value.checkInDateTime?.year

                if (isEditing && isSelectedDate) return@filter false

                it.checkInDateTime != null && it.checkOutDateTime != null
            }
            .flatMap { stay ->
                val checkInDateTime = stay.checkInDateTime!!
                val checkOutDateTime = stay.checkOutDateTime!!

                val isCheckInAlsoACheckOut = sortedPropertyStays.any { anyStay ->
                    checkInDateTime.toDate() == anyStay.checkOutDateTime?.toDate()
                }

                val isCheckOutAlsoACheckIn = sortedPropertyStays.any { anyStay ->
                    checkOutDateTime.toDate() == anyStay.checkInDateTime?.toDate()
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

    fun onStayTransferFormChange(form: StayTransfer?) {
        _transferFormErrors.update {
            val newErrors = it.errors.mutate { map ->
                if (transferForm.value?.transferDate != form?.transferDate) {
                    map[StayTransferField.TransferDate] = null
                }
                if (transferForm.value?.transferAmount != form?.transferAmount) {
                    map[StayTransferField.TransferAmount] = null
                }
            }

            it.copy(errors = newErrors)
        }

        _transferForm.update { form }
    }

    fun onEditTransfer(transfer: StayTransfer) {
        transferFormBeforeEdit = transfer
    }

    fun onDismissTransfer() {
        transferFormBeforeEdit = null
    }

    fun saveStayTransfer() {
        clearStayTransferErrors()

        viewModelScope.launch {
            if (transferForm.value == null) return@launch
            val isEditing = transferFormBeforeEdit != null

            if (isEditing) {
                val storedTransfer = stayForm.value.transfers
                    .find { it == transferFormBeforeEdit }

                if (transferForm.value == storedTransfer) {
                    _uiEvents.send(
                        UiEvents.TransferEditSuccess(messageId = R.string.no_data_changed_success_message)
                    )
                    return@launch
                }
            }

            stayTransferValidator.validate(stayTransfer = transferForm.value!!)
                .onErrors { errors ->
                    setTransferFieldErrors(errors)
                    return@launch
                }

            val newTransfers = if (isEditing) {
                stayForm.value.transfers
                    .toPersistentList()
                    .mutate { list ->
                        val i = list.indexOf(transferFormBeforeEdit)
                        list[i] = transferForm.value!!
                    }
            } else {
                stayForm.value.transfers
                    .toPersistentList()
                    .add(transferForm.value!!)
            }

            onStayFormChange(form = stayForm.value.copy(transfers = newTransfers))
            onStayTransferFormChange(form = null)
            transferFormBeforeEdit = null

            val saveEvent =
                if (stayToEdit != null) UiEvents.TransferEditSuccess(messageId = R.string.edit_stay_success_message)
                else UiEvents.TransferCreateSuccess

            _uiEvents.send(saveEvent)
        }
    }

    private suspend fun setTransferFieldErrors(errors: List<DomainError>) {
        _transferFormErrors.update { StayTransferErrorUiState() }
        delay(150.milliseconds)

        val transferDateError = errors.firstOrNull { it is DomainError.StayTransferDate }
        val transferAmountError = errors.firstOrNull { it is DomainError.StayTransferAmount }

        _transferFormErrors.update {
            val newErrors = it.errors.mutate { map ->
                map[StayTransferField.TransferDate] = transferDateError?.toInlineMessage()
                map[StayTransferField.TransferAmount] = transferAmountError?.toInlineMessage()
            }

            it.copy(errors = newErrors)
        }
    }

    private fun clearStayTransferErrors() {
        _transferFormErrors.update { StayTransferErrorUiState() }
    }

    fun onStayFilePick(contentUriString: String) {
        viewModelScope.launch {
            val newFile = StayFile(uriString = contentUriString)

            val fileName = stayFileRepository.getFileName(file = newFile)
                .fold(
                    onError = { e -> return@launch _uiEvents.send(UiEvents.Error(e)) },
                    onSuccess = { fileName -> fileName },
                )

            val newFiles =
                (stayForm.value.files + newFile.copy(name = fileName)).toPersistentList()
            onStayFormChange(form = stayForm.value.copy(files = newFiles))
        }
    }

    fun saveStay() {
        viewModelScope.launch {
            if (stayToEdit != null && stayForm.value == stayToEdit) {
                _uiEvents.send(UiEvents.StayEditSuccess(messageId = R.string.no_data_changed_success_message))
                return@launch
            }

            stayValidator.validate(
                stay = stayForm.value,
                stayBeforeEdit = stayToEdit,
            )
                .onErrors { errors ->
                    setFieldErrors(errors)
                    return@launch
                }

            stayRepository.insertOrUpdate(stayForm.value)
                .onError { e -> return@launch _uiEvents.send(UiEvents.Error(e)) }

            val editMessageId =
                if (stayForm.value.isUnavailablePeriod) R.string.edit_unavailable_period_success_message
                else R.string.edit_stay_success_message

            val saveEvent =
                if (stayToEdit != null) UiEvents.StayEditSuccess(messageId = editMessageId)
                else UiEvents.StayCreateSuccess

            _uiEvents.send(saveEvent)
        }
    }

    private suspend fun setFieldErrors(errors: List<DomainError>) {
        _stayFormErrors.update { StayErrorUiState() }
        delay(150.milliseconds)

        val stayPropertyError = errors.firstOrNull { it is DomainError.StayProperty }
        val stayTenantError = errors.firstOrNull { it is DomainError.StayTenant }
        val periodError = errors.firstOrNull { it is DomainError.StayPeriod }
        val checkInTimeError = errors.firstOrNull { it is DomainError.StayCheckInTime }
        val checkOutTimeError = errors.firstOrNull { it is DomainError.StayCheckOutTime }
        val commissionError = errors.firstOrNull { it is DomainError.StayCommission }

        _stayFormErrors.update {
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
            _stayFormErrors.value.errors.entries.first { it.value != null }.key
        _uiEvents.send(UiEvents.ScrollToField(firstFieldWithError))
    }

    fun onExit() {
        val needConfirmation = when {
            stayToEdit != null -> stayToEdit != stayForm.value
            else -> stayForm.value.isAnyFormFieldInitialized
        }

        viewModelScope.launch {
            _uiEvents.send(UiEvents.Exit(needConfirmation))
        }
    }

    sealed interface UiEvents {
        data class Error(val error: DataError) : UiEvents

        data class ScrollToField(val field: StayField) : UiEvents

        data class StayEditSuccess(val messageId: Int) : UiEvents

        data class TransferEditSuccess(val messageId: Int) : UiEvents

        data object StayCreateSuccess : UiEvents

        data object TransferCreateSuccess : UiEvents

        data class Exit(val needConfirmation: Boolean) : UiEvents
    }
}
