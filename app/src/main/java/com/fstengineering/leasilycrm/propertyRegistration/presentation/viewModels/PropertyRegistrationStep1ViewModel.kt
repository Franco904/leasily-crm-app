package com.fstengineering.leasilycrm.propertyRegistration.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.onErrors
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toInlineMessage
import com.fstengineering.leasilycrm.propertyRegistration.domain.entities.Property
import com.fstengineering.leasilycrm.propertyRegistration.presentation.models.PropertyDraftFormUiState
import com.fstengineering.leasilycrm.propertyRegistration.presentation.models.PropertyStep1ErrorUiState
import com.fstengineering.leasilycrm.propertyRegistration.presentation.models.fields.PropertyStep1Field
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.mutate
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class PropertyRegistrationStep1ViewModel : ViewModel() {
    private val _uiEvents = Channel<UiEvent>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _formErrors = MutableStateFlow(PropertyStep1ErrorUiState())
    val formErrors = _formErrors.asStateFlow()

    fun onPropertyFormChange(
        currentForm: PropertyDraftFormUiState.Loaded,
        newForm: PropertyDraftFormUiState.Loaded,
    ) {
        _formErrors.update {
            val newErrors = it.errors.mutate { map ->
                if (newForm.owner != currentForm.owner) {
                    map[PropertyStep1Field.PropertyOwner] = null
                }
                if (newForm.type != currentForm.type) {
                    map[PropertyStep1Field.PropertyType] = null
                }
                if (newForm.neighborhood != currentForm.neighborhood) {
                    map[PropertyStep1Field.Neighborhood] = null
                }
                if (newForm.streetAddress != currentForm.streetAddress) {
                    map[PropertyStep1Field.StreetAddress] = null
                }
            }

            it.copy(errors = newErrors)
        }
    }

    fun validatePropertyStep1(form: PropertyDraftFormUiState.Loaded) {
        Property.parse(
            ownerId = form.owner?.id,
            type = form.type,
            neighborhood = form.neighborhood,
            streetAddress = form.streetAddress,
        )
            .onErrors { errors -> setFieldErrors(errors) }
    }

    private fun setFieldErrors(errors: ImmutableList<DomainError>) {
        _formErrors.update { PropertyStep1ErrorUiState() }

        viewModelScope.launch {
            delay(150.milliseconds)

            val propertyOwnerError = errors.firstOrNull { it is DomainError.PropertyPropertyOwner }
            val propertyTypeError = errors.firstOrNull { it is DomainError.PropertyType }
            val propertyNeighborhoodError =
                errors.firstOrNull { it is DomainError.Neighborhood }
            val propertyStreetAddressError =
                errors.firstOrNull { it is DomainError.PropertyStreetAddress }

            _formErrors.update {
                val newErrors = it.errors.mutate { map ->
                    map[PropertyStep1Field.PropertyOwner] =
                        propertyOwnerError?.toInlineMessage()
                    map[PropertyStep1Field.PropertyType] =
                        propertyTypeError?.toInlineMessage()
                    map[PropertyStep1Field.Neighborhood] =
                        propertyNeighborhoodError?.toInlineMessage()
                    map[PropertyStep1Field.StreetAddress] =
                        propertyStreetAddressError?.toInlineMessage()
                }

                it.copy(errors = newErrors)
            }

            val currentErrors = _formErrors.value

            if (currentErrors.hasErrors) {
                val firstFieldWithError =
                    currentErrors.errors.entries.first { it.value != null }.key
                _uiEvents.trySend(UiEvent.ScrollToField(firstFieldWithError))
            } else {
                _uiEvents.trySend(UiEvent.ValidateSuccess)
            }
        }
    }

    override fun onCleared() {
        _uiEvents.close()

        super.onCleared()
    }

    sealed interface UiEvent {
        data class ScrollToField(val field: PropertyStep1Field) : UiEvent
        data object ValidateSuccess : UiEvent
    }
}
