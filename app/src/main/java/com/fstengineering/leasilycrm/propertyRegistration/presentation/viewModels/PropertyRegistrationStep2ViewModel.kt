package com.fstengineering.leasilycrm.propertyRegistration.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.onErrors
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toInlineMessage
import com.fstengineering.leasilycrm.propertyRegistration.domain.entities.Property
import com.fstengineering.leasilycrm.propertyRegistration.presentation.models.PropertyDraftFormUiState
import com.fstengineering.leasilycrm.propertyRegistration.presentation.models.PropertyStep2ErrorUiState
import com.fstengineering.leasilycrm.propertyRegistration.presentation.models.fields.PropertyStep2Field
import kotlinx.collections.immutable.mutate
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class PropertyRegistrationStep2ViewModel : ViewModel() {
    private val _uiEvents = Channel<UiEvent>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _formErrors = MutableStateFlow(PropertyStep2ErrorUiState())
    val formErrors = _formErrors.asStateFlow()

    fun onPropertyFormChange(
        currentForm: PropertyDraftFormUiState.Loaded,
        newForm: PropertyDraftFormUiState.Loaded,
    ) {
        _formErrors.update {
            val newErrors = it.errors.mutate { map ->
                if (newForm.distanceToSea != currentForm.distanceToSea) {
                    map[PropertyStep2Field.DistanceToSea] = null
                }
            }

            it.copy(errors = newErrors)
        }
    }

    fun validatePropertyStep2(form: PropertyDraftFormUiState.Loaded) {
        Property.parse(
            seaProximityTypeId = form.seaProximityTypeId,
            distanceToSea = form.distanceToSea,
        )
            .onErrors { errors -> setFieldErrors(errors) }
    }

    private fun setFieldErrors(errors: List<DomainError>) {
        _formErrors.update { PropertyStep2ErrorUiState() }

        viewModelScope.launch {
            delay(150.milliseconds)

            val distanceToSeaError =
                errors.firstOrNull { it is DomainError.PropertySeaProximityDistanceToSea }

            _formErrors.update {
                val newErrors = it.errors.mutate { map ->
                    map[PropertyStep2Field.DistanceToSea] = distanceToSeaError?.toInlineMessage()
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
        data class ScrollToField(val field: PropertyStep2Field) : UiEvent
        data object ValidateSuccess : UiEvent
    }
}
