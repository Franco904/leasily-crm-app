package com.fstengineering.leasilycrm.propertyRegistration.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.domain.models.Property
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.onErrors
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.domain.validators.interfaces.PropertyValidator
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toInlineMessage
import com.fstengineering.leasilycrm.propertyRegistration.models.PropertyStep1ErrorUiState
import com.fstengineering.leasilycrm.propertyRegistration.models.fields.PropertyStep1Field
import kotlinx.collections.immutable.mutate
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class PropertyRegistrationStep1ViewModel(
    private val propertyValidator: PropertyValidator,
) : ViewModel() {
    private val _uiEvents = Channel<UiEvents>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _formErrors = MutableStateFlow(PropertyStep1ErrorUiState())
    val formErrors = _formErrors.asStateFlow()

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

    fun validatePropertyStep1(propertyForm: Property) {
        viewModelScope.launch {
            propertyValidator.validate(property = propertyForm)
                .onErrors { errors -> setFieldErrors(errors) }
                .onSuccess { _uiEvents.send(UiEvents.ValidateSuccess) }
        }
    }

    private suspend fun setFieldErrors(errors: List<DomainError>) {
        _formErrors.update { PropertyStep1ErrorUiState() }
        delay(150.milliseconds)

        val propertyOwnerError = errors.firstOrNull { it is DomainError.PropertyPropertyOwner }
        val propertyTypeError = errors.firstOrNull { it is DomainError.PropertyType }
        val propertyNeighborhoodError =
            errors.firstOrNull { it is DomainError.PropertyNeighborhood }
        val propertyStreetAddressError =
            errors.firstOrNull { it is DomainError.PropertyStreetAddress }

        _formErrors.update {
            val newErrors = it.errors.mutate { map ->
                map[PropertyStep1Field.PropertyOwner] = propertyOwnerError?.toInlineMessage()
                map[PropertyStep1Field.PropertyType] = propertyTypeError?.toInlineMessage()
                map[PropertyStep1Field.Neighborhood] = propertyNeighborhoodError?.toInlineMessage()
                map[PropertyStep1Field.StreetAddress] =
                    propertyStreetAddressError?.toInlineMessage()
            }

            it.copy(errors = newErrors)
        }

        val firstFieldWithError = _formErrors.value.errors.entries.first { it.value != null }.key
        _uiEvents.send(UiEvents.ScrollToField(firstFieldWithError))
    }

    sealed interface UiEvents {
        data class ScrollToField(val field: PropertyStep1Field) : UiEvents
        data object ValidateSuccess : UiEvents
    }
}
