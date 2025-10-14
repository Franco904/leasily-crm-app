package com.fstengineering.leasilycrm.propertyOwnerRegistration

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.models.CustomerSegment
import com.fstengineering.leasilycrm.core.domain.models.PropertyOwner
import com.fstengineering.leasilycrm.core.domain.models.PropertyOwnerFile
import com.fstengineering.leasilycrm.core.domain.repositories.CustomerSegmentRepository
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyOwnerFileRepository
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyOwnerRepository
import com.fstengineering.leasilycrm.core.domain.repositories.UserRepository
import com.fstengineering.leasilycrm.core.domain.utils.ValidationError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onErrors
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.domain.validators.interfaces.PropertyOwnerValidator
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toUiMessage
import com.fstengineering.leasilycrm.propertyOwnerRegistration.models.PropertyOwnerErrorUiState
import com.fstengineering.leasilycrm.propertyOwnerRegistration.models.PropertyOwnerField
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class PropertyOwnerRegistrationViewModel(
    private val propertyOwnerRepository: PropertyOwnerRepository,
    private val propertyOwnerFileRepository: PropertyOwnerFileRepository,
    private val customerSegmentRepository: CustomerSegmentRepository,
    private val userRepository: UserRepository,
    private val propertyOwnerValidator: PropertyOwnerValidator,
) : ViewModel() {
    private var isInitialized = false

    private val _uiEvents = Channel<UiEvents>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _propertyOwnerForm = MutableStateFlow(PropertyOwner())
    val propertyOwnerForm = _propertyOwnerForm.asStateFlow()

    var propertyOwnerToEdit: PropertyOwner? = null
        private set

    private val _formErrors = MutableStateFlow(PropertyOwnerErrorUiState())
    val formErrors = _formErrors.asStateFlow()

    private val _propertyOwnerSegments =
        MutableStateFlow<ImmutableList<CustomerSegment>>(persistentListOf())
    val propertyOwnerSegments = _propertyOwnerSegments.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun init(propertyOwnerId: Long?) {
        if (isInitialized) return

        _isLoading.update { true }

        watchSessionExpiration()
        if (propertyOwnerId != null) getPropertyOwnerToEdit(propertyOwnerId)

        getPropertyOwnerSegments()

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

    private fun getPropertyOwnerToEdit(propertyOwnerId: Long) {
        viewModelScope.launch {
            propertyOwnerRepository.getById(propertyOwnerId)
                .onError { error ->
                    _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                }
                .onSuccess { storedPropertyOwner ->
                    _propertyOwnerForm.update { storedPropertyOwner }
                    propertyOwnerToEdit = storedPropertyOwner
                }
        }
    }

    private fun getPropertyOwnerSegments() {
        viewModelScope.launch {
            customerSegmentRepository.getAllPropertyOwnerType()
                .onError { error ->
                    _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                }
                .onSuccess { storedCustomerSegments ->
                    _propertyOwnerSegments.update { storedCustomerSegments }
                }

            _isLoading.update { false }
        }
    }

    fun onPropertyOwnerFormChange(form: PropertyOwner) {
        _formErrors.update {
            val newErrors = it.errors.mutate { map ->
                if (propertyOwnerForm.value.name != form.name) {
                    map[PropertyOwnerField.Name] = null
                }
                if (propertyOwnerForm.value.phone != form.phone) {
                    map[PropertyOwnerField.Phone] = null
                }
                if (propertyOwnerForm.value.email != form.email) {
                    map[PropertyOwnerField.Email] = null
                }
                if (propertyOwnerForm.value.paymentKey != form.paymentKey) {
                    map[PropertyOwnerField.PaymentKey] = null
                }
            }

            it.copy(errors = newErrors)
        }

        _propertyOwnerForm.update { form }
    }

    fun onPropertyOwnerFilePick(contentUriString: String) {
        viewModelScope.launch {
            val newFile = PropertyOwnerFile(uriString = contentUriString)

            val fileName = propertyOwnerFileRepository.getFileName(file = newFile)
                .fold(
                    onError = { error ->
                        _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                        return@launch
                    },
                    onSuccess = { fileName -> fileName },
                )

            val newFiles =
                (propertyOwnerForm.value.files + newFile.copy(name = fileName)).toPersistentList()
            onPropertyOwnerFormChange(form = propertyOwnerForm.value.copy(files = newFiles))
        }
    }

    fun savePropertyOwner() {
        viewModelScope.launch {
            val propertyOwner = propertyOwnerForm.value

            if (propertyOwnerToEdit != null && propertyOwner == propertyOwnerToEdit) {
                _uiEvents.send(UiEvents.PropertyOwnerEditSuccess(messageId = R.string.snackbar_no_data_changed))
                return@launch
            }

            val isThereStoredPropertyOwnerWithSameName = propertyOwner.name?.let {
                propertyOwnerRepository.isTherePropertyOwnerWithName(
                    propertyOwnerName = propertyOwner.name,
                    currentStoredPropertyOwnerName = propertyOwnerToEdit?.name,
                )
                    .fold(onError = { false }, onSuccess = { it })
            } ?: false

            propertyOwnerValidator.validate(
                propertyOwner = propertyOwner,
                isThereStoredPropertyOwnerWithSameName = isThereStoredPropertyOwnerWithSameName,
            )
                .onErrors { errors ->
                    setFieldErrors(errors)
                    return@launch
                }

            val propertyOwnerId = propertyOwnerRepository.insertOrUpdate(propertyOwner)
                .fold(
                    onError = { error ->
                        _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                        return@launch
                    },
                    onSuccess = { propertyOwnerId -> propertyOwnerId },
                )

            propertyOwner.files.forEach { file ->
                propertyOwnerFileRepository.insertOrUpdate(
                    file = file.copy(propertyOwnerId = propertyOwnerId),
                )
            }

            val saveEvent =
                if (propertyOwnerToEdit != null) UiEvents.PropertyOwnerEditSuccess(messageId = R.string.snackbar_edit_property_owner_succeeded)
                else UiEvents.PropertyOwnerCreateSuccess

            _uiEvents.send(saveEvent)
        }
    }

    private suspend fun setFieldErrors(errors: List<ValidationError>) {
        _formErrors.update { PropertyOwnerErrorUiState() }
        delay(150.milliseconds)

        val nameError = errors.firstOrNull { it is ValidationError.PropertyOwnerName }
        val phoneError = errors.firstOrNull { it is ValidationError.PropertyOwnerPhone }
        val emailError = errors.firstOrNull { it is ValidationError.PropertyOwnerEmail }
        val paymentKeyError =
            errors.firstOrNull { it is ValidationError.PropertyOwnerPaymentKey }

        _formErrors.update {
            val newErrors = it.errors.mutate { map ->
                map[PropertyOwnerField.Name] = nameError?.toUiMessage()
                map[PropertyOwnerField.Phone] = phoneError?.toUiMessage()
                map[PropertyOwnerField.Email] = emailError?.toUiMessage()
                map[PropertyOwnerField.PaymentKey] = paymentKeyError?.toUiMessage()
            }

            it.copy(errors = newErrors)
        }

        val firstFieldWithError = _formErrors.value.errors.entries.first { it.value != null }.key
        _uiEvents.send(UiEvents.ScrollToField(firstFieldWithError))
    }

    sealed interface UiEvents {
        data class DataSourceError(@StringRes val messageId: Int) : UiEvents

        data object RedirectToSignIn : UiEvents

        data class ScrollToField(val field: PropertyOwnerField) : UiEvents

        data object PropertyOwnerCreateSuccess : UiEvents

        data class PropertyOwnerEditSuccess(@StringRes val messageId: Int) : UiEvents
    }
}
