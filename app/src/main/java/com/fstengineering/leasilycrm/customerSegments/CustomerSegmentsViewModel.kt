package com.fstengineering.leasilycrm.customerSegments

import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.repositories.CustomerSegmentRepository
import com.fstengineering.leasilycrm.core.domain.repositories.CustomerSegmentTypeRepository
import com.fstengineering.leasilycrm.core.domain.repositories.UserRepository
import com.fstengineering.leasilycrm.core.domain.utils.ValidationError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onErrors
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.domain.validators.interfaces.CustomerSegmentValidator
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toUiMessage
import com.fstengineering.leasilycrm.customerSegments.models.CustomerSegmentErrorUiState
import com.fstengineering.leasilycrm.customerSegments.models.CustomerSegmentField
import com.fstengineering.leasilycrm.customerSegments.models.CustomerSegmentFormUiState
import com.fstengineering.leasilycrm.customerSegments.models.CustomerSegmentTypeUiState
import com.fstengineering.leasilycrm.profile.ProfileViewModel
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
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.internal.toImmutableList

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
        MutableStateFlow<ImmutableList<CustomerSegmentTypeUiState>>(persistentListOf())
    val customerSegmentTypes = _customerSegmentTypes.asStateFlow()

    private val _customerSegments =
        MutableStateFlow<ImmutableList<CustomerSegmentFormUiState>>(persistentListOf())

    val tenantCustomerSegments: StateFlow<ImmutableList<CustomerSegmentFormUiState>> =
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

    val propertyOwnerCustomerSegments: StateFlow<ImmutableList<CustomerSegmentFormUiState>> =
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

    private val _customerSegmentForm = MutableStateFlow<CustomerSegmentFormUiState?>(null)
    val customerSegmentForm = _customerSegmentForm.asStateFlow()

    private val _customerSegmentToDelete = MutableStateFlow<CustomerSegmentFormUiState?>(null)
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
                    val uiState = customerSegmentTypes
                        .map { CustomerSegmentTypeUiState.fromDomain(it) }
                        .toPersistentList()
                    _customerSegmentTypes.update { uiState }
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
                        val uiState = customerSegments
                            .map { CustomerSegmentFormUiState.fromDomain(it) }
                            .toPersistentList()
                        _customerSegments.update { uiState }
                    }

                _isLoading.update { false }
            }
        }
    }

    fun onCustomerSegmentFormChange(
        form: CustomerSegmentFormUiState?,
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
            if (isEditing) {
                val csFromForm = customerSegmentForm.value
                val storedCustomerSegment = _customerSegments.value.find { it.id == csFromForm?.id }

                if (csFromForm == storedCustomerSegment) {
                    _uiEvents.send(
                        UiEvents.CustomerSegmentEditSuccess(messageId = R.string.snackbar_no_data_changed)
                    )
                    return@launch
                }
            }

            val newCustomerSegment = customerSegmentForm.value?.toDomain() ?: return@launch

            customerSegmentValidator.validate(newCustomerSegment)
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

            if (isEditing) {
                customerSegmentRepository.update(customerSegment = newCustomerSegment)
                    .onError { error ->
                        if (error.isRedirectError()) {
                            userRepository.deleteCachedUser()
                            _uiEvents.send(UiEvents.RedirectToSignIn)
                        }

                        _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))

                        return@launch
                    }
            } else {
                customerSegmentRepository.insert(customerSegment = newCustomerSegment)
                    .onError { error ->
                        if (error.isRedirectError()) {
                            userRepository.deleteCachedUser()
                            _uiEvents.send(UiEvents.RedirectToSignIn)
                        }

                        _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))

                        return@launch
                    }
            }

            _uiEvents.send(
                if (isEditing) UiEvents.CustomerSegmentEditSuccess(messageId = R.string.snackbar_edit_customer_segment_succeeded)
                else UiEvents.CustomerSegmentCreateSuccess,
            )
        }
    }

    fun onDeleteCustomerSegment(
        customerSegment: CustomerSegmentFormUiState?,
    ) {
        _customerSegmentToDelete.update { customerSegment }
    }

    fun deleteCustomerSegment() {
        viewModelScope.launch {
            val csToDelete = customerSegmentToDelete.value?.toDomain() ?: return@launch

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

    private companion object {
        const val TENANT_CUSTOMER_SEGMENT_TYPE_ID = 1
        const val PROPERTY_OWNER_CUSTOMER_SEGMENT_TYPE_ID = 2
    }
}