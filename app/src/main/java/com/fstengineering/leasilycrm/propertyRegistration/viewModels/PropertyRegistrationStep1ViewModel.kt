package com.fstengineering.leasilycrm.propertyRegistration.viewModels

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.domain.models.Property
import com.fstengineering.leasilycrm.core.domain.models.PropertyOwner
import com.fstengineering.leasilycrm.core.domain.models.PropertyType
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyOwnerRepository
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyRepository
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyTypeRepository
import com.fstengineering.leasilycrm.core.domain.utils.ValidationError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onErrors
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.domain.validators.interfaces.PropertyValidator
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toUiMessage
import com.fstengineering.leasilycrm.propertyRegistration.models.PropertyStep1ErrorUiState
import com.fstengineering.leasilycrm.propertyRegistration.models.fields.PropertyStep1Field
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

class PropertyRegistrationStep1ViewModel(
    private val propertyRepository: PropertyRepository,
    private val propertyOwnerRepository: PropertyOwnerRepository,
    private val propertyTypeRepository: PropertyTypeRepository,
    private val propertyValidator: PropertyValidator,
) : ViewModel() {
    private var isInitialized = false

    private val _uiEvents = Channel<UiEvents>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _propertyOwners =
        MutableStateFlow<ImmutableList<PropertyOwner>>(persistentListOf())
    val propertyOwners = _propertyOwners.asStateFlow()

    private val _propertyTypes =
        MutableStateFlow<ImmutableList<PropertyType>>(persistentListOf())
    val propertyTypes = _propertyTypes.asStateFlow()

    private val _pastNeighborhoods =
        MutableStateFlow<ImmutableList<String>>(persistentListOf())
    val pastNeighborhoods = _pastNeighborhoods.asStateFlow()

    private val _formErrors = MutableStateFlow(PropertyStep1ErrorUiState())
    val formErrors = _formErrors.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun init() {
        if (isInitialized) return

        _isLoading.update { true }

        getPropertyOwners()
        getPropertyTypes()
        getPastNeighborhoods()

        isInitialized = true
    }

    private fun getPropertyOwners() {
        viewModelScope.launch {
            propertyOwnerRepository.watchAll().collect { result ->
                result
                    .onError { error ->
                        _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                    }
                    .onSuccess { storedPropertyOwners ->
                        _propertyOwners.update { storedPropertyOwners }
                    }
            }
        }
    }

    private fun getPropertyTypes() {
        viewModelScope.launch {
            propertyTypeRepository.getAll()
                .onError { error ->
                    _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                }
                .onSuccess { storedPropertyTypes ->
                    _propertyTypes.update { storedPropertyTypes }
                }
        }
    }

    private fun getPastNeighborhoods() {
        viewModelScope.launch {
            propertyRepository.getAllNeighborHoodNames()
                .onError { error ->
                    _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                }
                .onSuccess { neighborhoodNames ->
                    _pastNeighborhoods.update { neighborhoodNames }
                }

            _isLoading.update { false }
        }
    }

    fun onPropertyFormChange(
        propertyForm: Property,
        form: Property,
    ) {
        _formErrors.update {
            val newErrors = it.errors.mutate { map ->
                if (propertyForm.owner != form.owner) {
                    map[PropertyStep1Field.PropertyOwner] = null
                }
                if (propertyForm.type != form.type) {
                    map[PropertyStep1Field.PropertyType] = null
                }
                if (propertyForm.neighborhood != form.neighborhood) {
                    map[PropertyStep1Field.Neighborhood] = null
                }
                if (propertyForm.streetAddress != form.streetAddress) {
                    map[PropertyStep1Field.StreetAddress] = null
                }
            }

            it.copy(errors = newErrors)
        }
    }

    fun validatePropertyStep1(
        propertyForm: Property,
    ) {
        viewModelScope.launch {
            propertyValidator.validate(property = propertyForm)
                .onErrors { errors ->
                    setFieldErrors(errors)
                    return@launch
                }

            _uiEvents.send(UiEvents.ValidateSuccess)
        }
    }

    private suspend fun setFieldErrors(errors: List<ValidationError>) {
        _formErrors.update { PropertyStep1ErrorUiState() }
        delay(150.milliseconds)

        val propertyOwnerError = errors.firstOrNull { it is ValidationError.PropertyPropertyOwner }
        val propertyTypeError = errors.firstOrNull { it is ValidationError.PropertyType }
        val propertyNeighborhoodError =
            errors.firstOrNull { it is ValidationError.PropertyNeighborhood }
        val propertyStreetAddressError =
            errors.firstOrNull { it is ValidationError.PropertyStreetAddress }

        _formErrors.update {
            val newErrors = it.errors.mutate { map ->
                map[PropertyStep1Field.PropertyOwner] = propertyOwnerError?.toUiMessage()
                map[PropertyStep1Field.PropertyType] = propertyTypeError?.toUiMessage()
                map[PropertyStep1Field.Neighborhood] = propertyNeighborhoodError?.toUiMessage()
                map[PropertyStep1Field.StreetAddress] = propertyStreetAddressError?.toUiMessage()
            }

            it.copy(errors = newErrors)
        }

        val firstFieldWithError = _formErrors.value.errors.entries.first { it.value != null }.key
        _uiEvents.send(UiEvents.ScrollToField(firstFieldWithError))
    }

    sealed interface UiEvents {
        data class DataSourceError(@StringRes val messageId: Int) : UiEvents

        data class ScrollToField(val field: PropertyStep1Field) : UiEvents

        data object ValidateSuccess : UiEvents
    }
}
