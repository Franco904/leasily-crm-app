package com.fstengineering.leasilycrm.propertyOwnerRegistration

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.models.PropertyOwner
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyOwnerRepository
import com.fstengineering.leasilycrm.core.domain.repositories.UserRepository
import com.fstengineering.leasilycrm.core.domain.utils.ValidationError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onErrors
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.domain.validators.interfaces.PropertyOwnerValidator
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toUiMessage
import com.fstengineering.leasilycrm.customerSegments.CustomerSegmentsViewModel
import com.fstengineering.leasilycrm.customerSegments.models.CustomerSegmentErrorUiState
import com.fstengineering.leasilycrm.customerSegments.models.CustomerSegmentField
import com.fstengineering.leasilycrm.customerSegments.models.CustomerSegmentFormUiState
import com.fstengineering.leasilycrm.propertyOwnerRegistration.models.PropertyOwnerErrorUiState
import com.fstengineering.leasilycrm.propertyOwnerRegistration.models.PropertyOwnerField
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.collections.set

class PropertyOwnerRegistrationViewModel(
    private val propertyOwnerRepository: PropertyOwnerRepository,
    private val userRepository: UserRepository,
    private val propertyOwnerValidator: PropertyOwnerValidator,
) : ViewModel() {
    private var isInitialized = false

    private val _uiEvents = Channel<UiEvents>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _propertyOwnerForm = MutableStateFlow<PropertyOwner>(PropertyOwner())
    val propertyOwnerForm = _propertyOwnerForm.asStateFlow()

    var propertyOwnerToEdit: PropertyOwner? = null
        private set

    private val _formErrors = MutableStateFlow(PropertyOwnerErrorUiState())
    val formErrors = _formErrors.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun init(propertyOwnerId: Int?) {
        if (isInitialized) return

        watchSessionExpiration()
        if (propertyOwnerId != null) getPropertyOwnerToEdit(propertyOwnerId)

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

    private fun getPropertyOwnerToEdit(propertyOwnerId: Int) {
        _isLoading.update { true }

        viewModelScope.launch {
            propertyOwnerRepository.getById(propertyOwnerId)
                .onError { error ->
                    _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                }
                .onSuccess { storedPropertyOwner ->
                    _propertyOwnerForm.update { storedPropertyOwner }
                    propertyOwnerToEdit = storedPropertyOwner
                }

            _isLoading.update { false }
        }
    }

    fun onPropertyOwnerFormChange(form: PropertyOwner) {
        _formErrors.update {
            val newErrors = it.errors.mutate { map ->
                if (propertyOwnerForm.value?.name != form?.name) {
                    map[PropertyOwnerField.Name] = null
                }
                if (propertyOwnerForm.value?.phone != form?.phone) {
                    map[PropertyOwnerField.Phone] = null
                }
                if (propertyOwnerForm.value?.email != form?.email) {
                    map[PropertyOwnerField.Email] = null
                }
                if (propertyOwnerForm.value?.paymentKey != form?.paymentKey) {
                    map[PropertyOwnerField.PaymentKey] = null
                }
            }

            it.copy(errors = newErrors)
        }

        _propertyOwnerForm.update { form }
    }

    fun savePropertyOwner() {
        viewModelScope.launch {
            if (propertyOwnerToEdit != null && propertyOwnerForm.value == propertyOwnerToEdit) {
                _uiEvents.send(UiEvents.PropertyOwnerEditSuccess(messageId = R.string.snackbar_no_data_changed))
                return@launch
            }

            propertyOwnerValidator.validate(propertyOwner = propertyOwnerForm.value)
                .onErrors { errors ->
                    setFieldErrors(errors)
                    return@launch
                }

            propertyOwnerRepository.insertOrUpdate(propertyOwnerForm.value)
                .onError { error ->
                    if (error.isRedirectError()) {
                        userRepository.deleteCachedUser()
                        _uiEvents.send(UiEvents.RedirectToSignIn)
                    }

                    _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))

                    return@launch
                }

            _uiEvents.send(
                if (propertyOwnerToEdit != null) UiEvents.PropertyOwnerEditSuccess(messageId = R.string.snackbar_edit_property_owner_succeeded)
                else UiEvents.PropertyOwnerCreateSuccess,
            )
        }
    }

    private fun setFieldErrors(errors: List<ValidationError>) {
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
    }

    sealed interface UiEvents {
        data class DataSourceError(@StringRes val messageId: Int) : UiEvents

        data object RedirectToSignIn : UiEvents

        data object PropertyOwnerCreateSuccess : UiEvents

        data class PropertyOwnerEditSuccess(@StringRes val messageId: Int) : UiEvents
    }
}
