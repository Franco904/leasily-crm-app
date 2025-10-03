package com.fstengineering.leasilycrm.stayRegistration

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.models.Property
import com.fstengineering.leasilycrm.core.domain.models.Stay
import com.fstengineering.leasilycrm.core.domain.models.StayFile
import com.fstengineering.leasilycrm.core.domain.models.StayTransfer
import com.fstengineering.leasilycrm.core.domain.models.Tenant
import com.fstengineering.leasilycrm.core.domain.models.enums.TenantStatus
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyRepository
import com.fstengineering.leasilycrm.core.domain.repositories.StayFileRepository
import com.fstengineering.leasilycrm.core.domain.repositories.StayRepository
import com.fstengineering.leasilycrm.core.domain.repositories.StayTransferRepository
import com.fstengineering.leasilycrm.core.domain.repositories.TenantRepository
import com.fstengineering.leasilycrm.core.domain.repositories.UserRepository
import com.fstengineering.leasilycrm.core.domain.utils.ValidationError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onErrors
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.domain.validators.interfaces.StayTransferValidator
import com.fstengineering.leasilycrm.core.domain.validators.interfaces.StayValidator
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toUiMessage
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
import java.time.LocalDate
import kotlin.time.Duration.Companion.milliseconds

class StayRegistrationViewModel(
    private val stayRepository: StayRepository,
    private val propertyRepository: PropertyRepository,
    private val tenantRepository: TenantRepository,
    private val stayTransferRepository: StayTransferRepository,
    private val stayFileRepository: StayFileRepository,
    private val userRepository: UserRepository,
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

    var stayToEdit: Stay? = null
        private set

    var transferFormBeforeEdit: StayTransfer? = null

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
        isStayFromOtherRealtor: Boolean,
    ) {
        if (isInitialized) return

        _isLoading.update { true }

        watchSessionExpiration()
        if (stayId != null) getStayToEdit(stayId)

        if (stayId == null && propertyId != null) getPropertyToAutofill(propertyId)
        if (stayId == null && tenantId != null) getTenantToAutofill(tenantId)

        _stayForm.update { it.copy(isFromOtherRealtor = isStayFromOtherRealtor) }

        watchPropertySearchInput()
        watchTenantSearchInput()

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

    private fun getStayToEdit(stayId: Long) {
        viewModelScope.launch {
            stayRepository.getById(stayId)
                .onError { error ->
                    _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                }
                .onSuccess { storedStay ->
                    _stayForm.update { storedStay }
                    stayToEdit = storedStay
                }
        }
    }

    private fun getPropertyToAutofill(propertyId: Long) {
        viewModelScope.launch {
            propertyRepository.getById(propertyId)
                .onError { error ->
                    _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                }
                .onSuccess { storedProperty ->
                    _stayForm.update { it.copy(property = storedProperty) }
                }
        }
    }

    private fun getTenantToAutofill(tenantId: Long) {
        viewModelScope.launch {
            tenantRepository.getById(tenantId)
                .onError { error ->
                    _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                }
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
                        .onError { error ->
                            _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                        }
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

                    tenantRepository.getAllWithoutUpcomingStaysMatching(matching = searchInput)
                        .onError { error ->
                            _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                        }
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
                if (stayForm.value.totalPrice != form.totalPrice) {
                    map[StayField.TotalPrice] = null
                }
            }

            it.copy(errors = newErrors)
        }

        _stayForm.update { form }
    }

    fun getPropertyAlreadySelectedDatesForStay(
        propertyStays: ImmutableList<Stay>,
    ): ImmutableSet<LocalDate> {
        val isEditing = stayToEdit != null

        return propertyStays
            .asSequence()
            .filter {
                val isSelectedDate =
                    it.checkInDateTime?.dayOfMonth == stayForm.value.checkInDateTime?.dayOfMonth &&
                            it.checkInDateTime?.monthValue == stayForm.value.checkInDateTime?.monthValue &&
                            it.checkInDateTime?.year == stayForm.value.checkInDateTime?.year

                if (isEditing && isSelectedDate) return@filter false

                it.checkInDateTime != null && it.checkOutDateTime != null
            }
            .flatMap { stay ->
                val checkInDateTime = stay.checkInDateTime!!
                val checkOutDateTime = stay.checkOutDateTime!!

                generateSequence(checkInDateTime) { it.plusDays(1) }
                    .takeWhile { it <= checkOutDateTime }
                    .map { it.toLocalDate() }
            }
            .toPersistentSet()
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
                        UiEvents.TransferEditSuccess(messageId = R.string.snackbar_no_data_changed)
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

            _uiEvents.send(
                if (stayToEdit != null) UiEvents.TransferEditSuccess(messageId = R.string.snackbar_edit_stay_succeeded)
                else UiEvents.TransferCreateSuccess,
            )
        }
    }

    private suspend fun setTransferFieldErrors(errors: List<ValidationError>) {
        _transferFormErrors.update { StayTransferErrorUiState() }
        delay(150.milliseconds)

        val transferDateError = errors.firstOrNull { it is ValidationError.StayTransferDate }
        val transferAmountError = errors.firstOrNull { it is ValidationError.StayTransferAmount }

        _transferFormErrors.update {
            val newErrors = it.errors.mutate { map ->
                map[StayTransferField.TransferDate] = transferDateError?.toUiMessage()
                map[StayTransferField.TransferAmount] = transferAmountError?.toUiMessage()
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
                    onError = { error ->
                        _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                        return@launch
                    },
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
                _uiEvents.send(UiEvents.StayEditSuccess(messageId = R.string.snackbar_no_data_changed))
                return@launch
            }

            stayValidator.validate(stay = stayForm.value)
                .onErrors { errors ->
                    setFieldErrors(errors)
                    return@launch
                }

            val stayId = stayRepository.insertOrUpdate(stayForm.value)
                .fold(
                    onError = { error ->
                        _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                        return@launch
                    },
                    onSuccess = { stayId -> stayId },
                )

            stayForm.value.files.forEach { file ->
                stayFileRepository.insertOrUpdate(
                    file = file.copy(stayId = stayId),
                )
            }

            stayForm.value.transfers.forEach { transfer ->
                stayTransferRepository.insertOrUpdate(
                    transfer = transfer.copy(stayId = stayId),
                )
            }

            if (stayForm.value.tenant != null) {
                val updatedTenant = stayForm.value.tenant
                    ?.copy(status = TenantStatus.UpcomingStay.id)
                    ?: return@launch

                tenantRepository.insertOrUpdate(updatedTenant)
                    .onError { error ->
                        _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                    }
            }

            _uiEvents.send(
                if (stayToEdit != null) UiEvents.StayEditSuccess(messageId = R.string.snackbar_edit_stay_succeeded)
                else UiEvents.StayCreateSuccess,
            )
        }
    }

    private suspend fun setFieldErrors(errors: List<ValidationError>) {
        _stayFormErrors.update { StayErrorUiState() }
        delay(150.milliseconds)

        val stayPropertyError = errors.firstOrNull { it is ValidationError.StayProperty }
        val stayTenantError = errors.firstOrNull { it is ValidationError.StayTenant }
        val periodError = errors.firstOrNull { it is ValidationError.StayPeriod }
        val checkInTimeError = errors.firstOrNull { it is ValidationError.StayCheckInTime }
        val checkOutTimeError = errors.firstOrNull { it is ValidationError.StayCheckOutTime }
        val totalPriceError = errors.firstOrNull { it is ValidationError.StayTotalPrice }

        _stayFormErrors.update {
            val newErrors = it.errors.mutate { map ->
                map[StayField.Property] = stayPropertyError?.toUiMessage()
                map[StayField.Tenant] = stayTenantError?.toUiMessage()
                map[StayField.Period] = periodError?.toUiMessage()
                map[StayField.CheckInTime] = checkInTimeError?.toUiMessage()
                map[StayField.CheckOutTime] = checkOutTimeError?.toUiMessage()
                map[StayField.TotalPrice] = totalPriceError?.toUiMessage()
            }

            it.copy(errors = newErrors)
        }

        val firstFieldWithError =
            _stayFormErrors.value.errors.entries.first { it.value != null }.key
        _uiEvents.send(UiEvents.ScrollToField(firstFieldWithError))
    }

    sealed interface UiEvents {
        data class DataSourceError(@StringRes val messageId: Int) : UiEvents

        data object RedirectToSignIn : UiEvents

        data class ScrollToField(val field: StayField) : UiEvents

        data object StayCreateSuccess : UiEvents

        data class StayEditSuccess(@StringRes val messageId: Int) : UiEvents

        data object TransferCreateSuccess : UiEvents

        data class TransferEditSuccess(@StringRes val messageId: Int) : UiEvents
    }
}
