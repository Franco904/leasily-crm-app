package com.fstengineering.leasilycrm.propertyRegistration.viewModels

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.domain.models.Property
import com.fstengineering.leasilycrm.core.domain.utils.ValidationError
import com.fstengineering.leasilycrm.core.domain.utils.onErrors
import com.fstengineering.leasilycrm.core.domain.validators.interfaces.PropertyValidator
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toUiMessage
import com.fstengineering.leasilycrm.propertyRegistration.models.PropertyStep2ErrorUiState
import com.fstengineering.leasilycrm.propertyRegistration.models.fields.PropertyStep2Field
import kotlinx.collections.immutable.mutate
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class PropertyRegistrationStep2ViewModel(
    private val propertyValidator: PropertyValidator,
) : ViewModel() {
    private val _uiEvents = Channel<UiEvents>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _formErrors = MutableStateFlow(PropertyStep2ErrorUiState())
    val formErrors = _formErrors.asStateFlow()

    fun onPropertyFormChange(
        propertyForm: Property,
        form: Property,
    ) {
        _formErrors.update {
            val newErrors = it.errors.mutate { map ->
                if (propertyForm.distanceToSea != form.distanceToSea) {
                    map[PropertyStep2Field.DistanceToSea] = null
                }
            }

            it.copy(errors = newErrors)
        }
    }

    fun validatePropertyStep2(
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
        _formErrors.update { PropertyStep2ErrorUiState() }
        delay(150.milliseconds)

        val propertyDistanceToSeaError =
            errors.firstOrNull { it is ValidationError.PropertyDistanceToSea }

        _formErrors.update {
            val newErrors = it.errors.mutate { map ->
                map[PropertyStep2Field.DistanceToSea] =
                    propertyDistanceToSeaError?.toUiMessage()
            }

            it.copy(errors = newErrors)
        }

        val firstFieldWithError = _formErrors.value.errors.entries.first { it.value != null }.key
        _uiEvents.send(UiEvents.ScrollToField(firstFieldWithError))
    }

    sealed interface UiEvents {
        data class DataSourceError(@StringRes val messageId: Int) : UiEvents

        data class ScrollToField(val field: PropertyStep2Field) : UiEvents

        data object ValidateSuccess : UiEvents
    }
}
