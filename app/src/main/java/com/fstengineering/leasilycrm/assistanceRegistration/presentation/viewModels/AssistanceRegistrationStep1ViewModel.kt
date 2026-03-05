package com.fstengineering.leasilycrm.assistanceRegistration.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.assistanceRegistration.presentation.models.AssistanceDraftFormUiState
import com.fstengineering.leasilycrm.assistanceRegistration.presentation.models.AssistanceStep1ErrorUiState
import com.fstengineering.leasilycrm.assistanceRegistration.presentation.models.fields.AssistanceStep1Field
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.onErrors
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
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
    private val _uiEvents = Channel<UiEvent>()
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
            _formErrors.update { AssistanceStep1ErrorUiState() }
            delay(150.milliseconds)

            if (assistanceForm.preferredSeaProximityTypeId == null) return@launch

            val parsingResults = PropertySeaProximityType.parse(
                id = assistanceForm.preferredSeaProximityTypeId,
                distanceToSea = assistanceForm.preferredDistanceToSea,
            )

            parsingResults
                .onSuccess { _uiEvents.send(UiEvent.ValidateSuccess) }
                .onErrors { errors ->
                    val distanceToSeaError =
                        errors.firstOrNull { it is DomainError.PropertySeaProximity }

                    _formErrors.update {
                        val newErrors = it.errors.mutate { map ->
                            map[AssistanceStep1Field.DistanceToSea] =
                                distanceToSeaError?.toInlineMessage()
                        }

                        it.copy(errors = newErrors)
                    }

                    val firstFieldWithError =
                        _formErrors.value.errors.entries.first { it.value != null }.key
                    _uiEvents.send(UiEvent.ScrollToField(firstFieldWithError))
                }
        }
    }

    fun savePreferredNeighborhood() {
        viewModelScope.launch {
            val newNeighborhood = neighborhoodForm.value

            if (newNeighborhood.isNullOrBlank()) {
                val error = DomainError.Neighborhood.Required
                _neighborhoodFormError.update { error.toInlineMessage() }

                return@launch
            }

            clearPreferredNeighborhoodErrors()
            _uiEvents.send(UiEvent.AssistanceSearchedNeighborhoodSaveSuccess)
        }
    }

    fun clearPreferredNeighborhoodErrors() {
        _neighborhoodFormError.update { null }
    }

    sealed interface UiEvent {
        data class ScrollToField(val field: AssistanceStep1Field) : UiEvent

        data object ValidateSuccess : UiEvent

        data object AssistanceSearchedNeighborhoodSaveSuccess : UiEvent
    }
}
