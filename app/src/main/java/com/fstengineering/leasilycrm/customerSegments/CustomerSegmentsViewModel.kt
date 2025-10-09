package com.fstengineering.leasilycrm.customerSegments

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.constants.PROPERTY_OWNER_CUSTOMER_SEGMENT_TYPE_ID
import com.fstengineering.leasilycrm.core.domain.constants.TENANT_CUSTOMER_SEGMENT_TYPE_ID
import com.fstengineering.leasilycrm.core.domain.models.CustomerSegment
import com.fstengineering.leasilycrm.core.domain.models.CustomerSegmentType
import com.fstengineering.leasilycrm.core.domain.repositories.CustomerSegmentRepository
import com.fstengineering.leasilycrm.core.domain.repositories.CustomerSegmentTypeRepository
import com.fstengineering.leasilycrm.core.domain.repositories.UserRepository
import com.fstengineering.leasilycrm.core.domain.utils.ValidationError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onErrors
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.domain.validators.interfaces.CustomerSegmentValidator
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toUiMessage
import com.fstengineering.leasilycrm.customerSegments.models.CustomerSegmentErrorUiState
import com.fstengineering.leasilycrm.customerSegments.models.CustomerSegmentField
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CustomerSegmentsViewModel(
    private val customerSegmentRepository: CustomerSegmentRepository,
    private val customerSegmentTypeRepository: CustomerSegmentTypeRepository,
    private val userRepository: UserRepository,
    private val customerSegmentValidator: CustomerSegmentValidator,
) : ViewModel() {
    private var isInitialized = false

    private val _uiEvents = Channel<UiEvents>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _customerSegmentTypes =
        MutableStateFlow<ImmutableList<CustomerSegmentType>>(persistentListOf())
    val customerSegmentTypes = _customerSegmentTypes.asStateFlow()

    private val _customerSegments =
        MutableStateFlow<ImmutableList<CustomerSegment>>(persistentListOf())

    val tenantCustomerSegments: StateFlow<ImmutableList<CustomerSegment>> =
        _customerSegments
            .map {
                it.filter { segment -> segment.type?.id == TENANT_CUSTOMER_SEGMENT_TYPE_ID }
                    .toPersistentList()
            }
            .distinctUntilChanged()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = persistentListOf(),
            )

    val propertyOwnerCustomerSegments: StateFlow<ImmutableList<CustomerSegment>> =
        _customerSegments
            .map {
                it.filter { segment -> segment.type?.id == PROPERTY_OWNER_CUSTOMER_SEGMENT_TYPE_ID }
                    .toPersistentList()
            }
            .distinctUntilChanged()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = persistentListOf(),
            )

    private val _customerSegmentForm = MutableStateFlow<CustomerSegment?>(null)
    val customerSegmentForm = _customerSegmentForm.asStateFlow()

    private val _customerSegmentToDelete = MutableStateFlow<CustomerSegment?>(null)
    val customerSegmentToDelete = _customerSegmentToDelete.asStateFlow()

    private val _formErrors = MutableStateFlow(CustomerSegmentErrorUiState())
    val formErrors = _formErrors.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun init() {
        if (isInitialized) return

        watchSessionExpiration()
        getCustomerSegmentTypes()
        watchCustomerSegments()

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

    private fun getCustomerSegmentTypes() {
        viewModelScope.launch {
            customerSegmentTypeRepository.getAll()
                .onError { error ->
                    _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                }
                .onSuccess { customerSegmentTypes ->
                    _customerSegmentTypes.update { customerSegmentTypes }
                }
        }
    }

    private fun watchCustomerSegments() {
        _isLoading.update { true }

        viewModelScope.launch {
            customerSegmentRepository.watchAll().collect { result ->
                result
                    .onError { error ->
                        _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                    }
                    .onSuccess { customerSegments ->
                        _customerSegments.update { customerSegments }
                    }

                _isLoading.update { false }
            }
        }
    }

    fun onCustomerSegmentFormChange(
        form: CustomerSegment?,
    ) {
        _formErrors.update {
            val newErrors = it.errors.mutate { map ->
                if (customerSegmentForm.value?.name != form?.name) {
                    map[CustomerSegmentField.Name] = null
                }
                if (customerSegmentForm.value?.type != form?.type) {
                    map[CustomerSegmentField.Type] = null
                }
            }

            it.copy(errors = newErrors)
        }

        _customerSegmentForm.update { form }
    }

    fun saveCustomerSegment(isEditing: Boolean) {
        viewModelScope.launch {
            val csFromForm = customerSegmentForm.value
            val storedCustomerSegment = _customerSegments.value.find { it.id == csFromForm?.id }

            if (isEditing) {
                if (csFromForm == storedCustomerSegment) {
                    _uiEvents.send(
                        UiEvents.CustomerSegmentEditSuccess(messageId = R.string.snackbar_no_data_changed)
                    )

                    onCustomerSegmentFormChange(form = null)
                    return@launch
                }
            }

            val newCustomerSegment = customerSegmentForm.value ?: return@launch

            val isThereStoredCustomerSegmentWithSameName = when {
                newCustomerSegment.name != null && newCustomerSegment.type?.id != null -> {
                    customerSegmentRepository.isThereCustomerSegmentWithName(
                        customerSegmentName = newCustomerSegment.name,
                        customerSegmentTypeId = newCustomerSegment.type.id,
                        currentStoredCustomerSegmentName = storedCustomerSegment?.name,
                    )
                        .fold(onError = { false }, onSuccess = { it })
                }

                else -> false
            }

            customerSegmentValidator.validate(
                customerSegment = newCustomerSegment,
                isThereStoredCustomerSegmentWithSameName = isThereStoredCustomerSegmentWithSameName,
            )
                .onErrors { errors ->
                    val nameError = errors.firstOrNull { it is ValidationError.CustomerSegmentName }
                    val typeError = errors.firstOrNull { it is ValidationError.CustomerSegmentType }

                    _formErrors.update {
                        val newErrors = it.errors.mutate { map ->
                            map[CustomerSegmentField.Name] = nameError?.toUiMessage()
                            map[CustomerSegmentField.Type] = typeError?.toUiMessage()
                        }

                        it.copy(errors = newErrors)
                    }

                    return@launch
                }

            customerSegmentRepository.insertOrUpdate(customerSegment = newCustomerSegment)
                .onError { error ->
                    if (error.isRedirectError()) {
                        userRepository.deleteCachedUser()
                        _uiEvents.send(UiEvents.RedirectToSignIn)
                    }

                    _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))

                    return@launch
                }

            _uiEvents.send(
                if (isEditing) UiEvents.CustomerSegmentEditSuccess(messageId = R.string.snackbar_edit_customer_segment_succeeded)
                else UiEvents.CustomerSegmentCreateSuccess,
            )

            onCustomerSegmentFormChange(form = null)
        }
    }

    fun onDeleteCustomerSegment(
        customerSegment: CustomerSegment?,
    ) {
        _customerSegmentToDelete.update { customerSegment }
    }

    fun deleteCustomerSegment() {
        viewModelScope.launch {
            val csToDelete = customerSegmentToDelete.value ?: return@launch

            customerSegmentRepository.delete(csToDelete.id!!)
                .onError { error ->
                    if (error.isRedirectError()) {
                        userRepository.deleteCachedUser()
                        _uiEvents.send(UiEvents.RedirectToSignIn)
                    }

                    _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))

                    return@launch
                }

            _uiEvents.send(UiEvents.CustomerSegmentDeleteSuccess)

            onDeleteCustomerSegment(customerSegment = null)
        }
    }

    fun clearErrors() {
        _formErrors.update { CustomerSegmentErrorUiState() }
    }

    sealed interface UiEvents {
        data class DataSourceError(@StringRes val messageId: Int) : UiEvents

        data object RedirectToSignIn : UiEvents

        data object CustomerSegmentCreateSuccess : UiEvents

        data class CustomerSegmentEditSuccess(@StringRes val messageId: Int) : UiEvents

        data object CustomerSegmentDeleteSuccess : UiEvents
    }
}