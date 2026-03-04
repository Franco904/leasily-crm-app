package com.fstengineering.leasilycrm.assistanceRegistration.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.assistanceRegistration.presentation.models.AssistanceDraftFormUiState
import com.fstengineering.leasilycrm.assistanceRegistration.presentation.models.AssistanceStep1ErrorUiState
import com.fstengineering.leasilycrm.assistanceRegistration.presentation.models.fields.AssistanceStep1Field
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.domain.valueObjects.Neighborhood
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertySeaProximityType
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toInlineMessage
import kotlinx.collections.immutable.mutate
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class AssistanceRegistrationStep1ViewModel : ViewModel() {
    private val _uiEvents = Channel<UiEvents>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _formErrors = MutableStateFlow(AssistanceStep1ErrorUiState())
    val formErrors = _formErrors.asStateFlow()

    private val _neighborhoodForm = MutableStateFlow<String?>(null)
    val neighborhoodForm = _neighborhoodForm.asStateFlow()

    private val _neighborhoodFormError = MutableStateFlow<Int?>(null)
    val neighborhoodFormError = _neighborhoodFormError.asStateFlow()

    fun onAssistanceFormChange(
        newForm: AssistanceDraftFormUiState.Loaded,
        oldForm: AssistanceDraftFormUiState.Loaded,
    ) {
        _formErrors.update {
            val newErrors = it.errors.mutate { map ->
                if (oldForm.preferredDistanceToSea != newForm.preferredDistanceToSea) {
                    map[AssistanceStep1Field.DistanceToSea] = null
                }
            }

            it.copy(errors = newErrors)
        }
    }

    fun onNeighborhoodChange(neighborhood: String?) {
        _neighborhoodForm.update { neighborhood }
    }

    fun validateAssistanceStep1(assistanceForm: AssistanceDraftFormUiState.Loaded) {
        viewModelScope.launch {
            assistanceValidator.validate(assistance = assistanceForm)
                .onErrors { errors ->
                    setFieldErrors(errors)
                    return@launch
                }

            _uiEvents.send(UiEvents.ValidateSuccess)
        }
    }

    fun savePreferredNeighborhood() {
        viewModelScope.launch {
            val newNeighborhood = neighborhoodForm.value ?: return@launch

            val validationResult = Neighborhood.parse(newNeighborhood)

            validationResult
                .onError { e -> _neighborhoodFormError.update { e.toInlineMessage() } }
                .onSuccess {
                    clearPreferredNeighborhoodErrors()
                    _uiEvents.send(UiEvents.AssistanceSearchedNeighborhoodSaveSuccess)
                }
        }
    }

    fun clearPreferredNeighborhoodErrors() {
        _neighborhoodFormError.update { null }
    }

    private suspend fun setFieldErrors(errors: List<DomainError>) {
        _formErrors.update { AssistanceStep1ErrorUiState() }
        delay(150.milliseconds)

        val propertyDistanceToSeaError =
            errors.firstOrNull { it is DomainError.PropertySeaProximity }

        _formErrors.update {
            val newErrors = it.errors.mutate { map ->
                map[AssistanceStep1Field.DistanceToSea] =
                    propertyDistanceToSeaError?.toInlineMessage()
            }

            it.copy(errors = newErrors)
        }

        val firstFieldWithError = _formErrors.value.errors.entries.first { it.value != null }.key
        _uiEvents.send(UiEvents.ScrollToField(firstFieldWithError))
    }

    sealed interface UiEvents {
        data class ScrollToField(val field: AssistanceStep1Field) : UiEvents

        data object ValidateSuccess : UiEvents

        data object AssistanceSearchedNeighborhoodSaveSuccess : UiEvents
    }
}
