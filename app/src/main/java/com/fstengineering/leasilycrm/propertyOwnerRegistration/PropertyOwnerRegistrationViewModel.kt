package com.fstengineering.leasilycrm.propertyOwnerRegistration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.models.CustomerSegment
import com.fstengineering.leasilycrm.core.domain.models.PropertyOwner
import com.fstengineering.leasilycrm.core.domain.models.PropertyOwnerFile
import com.fstengineering.leasilycrm.core.domain.repositories.CustomerSegmentRepository
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyOwnerFileRepository
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyOwnerRepository
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onErrors
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.domain.validators.interfaces.PropertyOwnerValidator
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toInlineMessage
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

        if (propertyOwnerId != null) getPropertyOwnerToEdit(propertyOwnerId)

        isInitialized = true
    }

    private fun getPropertyOwnerToEdit(propertyOwnerId: Long) {
        viewModelScope.launch {
            propertyOwnerRepository.getById(propertyOwnerId)
                .onError { e -> _uiEvents.send(UiEvents.Error(e)) }
                .onSuccess { storedPropertyOwner ->
                    _propertyOwnerForm.update { storedPropertyOwner }
                    propertyOwnerToEdit = storedPropertyOwner
                }
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
                    onError = { e -> return@launch _uiEvents.send(UiEvents.Error(e)) },
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
                _uiEvents.send(UiEvents.PropertyOwnerEditSuccess(messageId = R.string.no_data_changed_success_message))
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

            propertyOwnerRepository.insertOrUpdate(propertyOwner)
                .onError { e -> return@launch _uiEvents.send(UiEvents.Error(e)) }

            val saveEvent =
                if (propertyOwnerToEdit != null) UiEvents.PropertyOwnerEditSuccess(messageId = R.string.edit_property_owner_success_message)
                else UiEvents.PropertyOwnerCreateSuccess

            _uiEvents.send(saveEvent)
        }
    }

    private suspend fun setFieldErrors(errors: List<DomainError>) {
        _formErrors.update { PropertyOwnerErrorUiState() }
        delay(150.milliseconds)

        val nameError = errors.firstOrNull { it is DomainError.PropertyOwnerName }
        val phoneError = errors.firstOrNull { it is DomainError.PropertyOwnerPhone }
        val emailError = errors.firstOrNull { it is DomainError.PropertyOwnerEmail }
        val paymentKeyError =
            errors.firstOrNull { it is DomainError.PropertyOwnerPaymentKey }

        _formErrors.update {
            val newErrors = it.errors.mutate { map ->
                map[PropertyOwnerField.Name] = nameError?.toInlineMessage()
                map[PropertyOwnerField.Phone] = phoneError?.toInlineMessage()
                map[PropertyOwnerField.Email] = emailError?.toInlineMessage()
                map[PropertyOwnerField.PaymentKey] = paymentKeyError?.toInlineMessage()
            }

            it.copy(errors = newErrors)
        }

        val firstFieldWithError = _formErrors.value.errors.entries.first { it.value != null }.key
        _uiEvents.send(UiEvents.ScrollToField(firstFieldWithError))
    }

    sealed interface UiEvents {
        data class Error(val error: DataError) : UiEvents
        data class ScrollToField(val field: PropertyOwnerField) : UiEvents
        data object PropertyOwnerCreateSuccess : UiEvents
        data class PropertyOwnerEditSuccess(val messageId: Int) : UiEvents
    }
}
