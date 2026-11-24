package com.fstengineering.leasilycrm.atendimentoRegistration.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.atendimentoRegistration.presentation.models.AtendimentoDraftFormUiState
import com.fstengineering.leasilycrm.atendimentoRegistration.presentation.models.AtendimentoStep1ErrorUiState
import com.fstengineering.leasilycrm.atendimentoRegistration.presentation.models.fields.AtendimentoStep1Field
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.domain.utils.toDomainErrors
import com.fstengineering.leasilycrm.core.domain.valueObjects.ImovelSeaProximityType
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

class AtendimentoRegistrationStep1ViewModel : ViewModel() {
    private val _uiEvents = Channel<UiEvent>(Channel.BUFFERED)
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _formErrors = MutableStateFlow(AtendimentoStep1ErrorUiState())
    val formErrors = _formErrors.asStateFlow()

    private val _neighborhoodForm = MutableStateFlow<String?>(null)
    val neighborhoodForm = _neighborhoodForm.asStateFlow()

    private val _neighborhoodFormError = MutableStateFlow<Int?>(null)
    val neighborhoodFormError = _neighborhoodFormError.asStateFlow()

    fun onAtendimentoFormChange(
        newForm: AtendimentoDraftFormUiState.Loaded,
        oldForm: AtendimentoDraftFormUiState.Loaded,
    ) {
        _formErrors.update {
            val newErrors = it.errors.mutate { map ->
                if (oldForm.preferredDistanceToSea != newForm.preferredDistanceToSea) {
                    map[AtendimentoStep1Field.DistanceToSea] = null
                }
            }

            it.copy(errors = newErrors)
        }
    }

    fun onNeighborhoodChange(neighborhood: String?) {
        _neighborhoodForm.update { neighborhood }
    }

    fun validateAtendimentoStep1(atendimentoForm: AtendimentoDraftFormUiState.Loaded) {
        viewModelScope.launch {
            _formErrors.update { AtendimentoStep1ErrorUiState() }
            delay(150.milliseconds)

            if (atendimentoForm.needValidation) {
                atendimentoForm.validate()
            } else {
                _uiEvents.trySend(UiEvent.ValidateSuccess)
            }
        }
    }

    private fun AtendimentoDraftFormUiState.Loaded.validate() {
        requireNotNull(preferredSeaProximityTypeId)

        val parsingResults = ImovelSeaProximityType.parse(
            id = preferredSeaProximityTypeId,
            distanceToSea = preferredDistanceToSea,
        )

        parsingResults
            .onSuccess { _uiEvents.trySend(UiEvent.ValidateSuccess) }
            .onError { error ->
                val distanceToSeaError =
                    error.toDomainErrors()
                        .firstOrNull { it is DomainError.ImovelSeaProximityDistanceToSea }

                _formErrors.update {
                    val newErrors = it.errors.mutate { map ->
                        map[AtendimentoStep1Field.DistanceToSea] =
                            distanceToSeaError?.toInlineMessage()
                    }

                    it.copy(errors = newErrors)
                }

                val firstFieldWithError =
                    _formErrors.value.errors.entries.first { it.value != null }.key
                _uiEvents.trySend(UiEvent.ScrollToField(firstFieldWithError))
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
            _uiEvents.trySend(UiEvent.FinishSearchedNeighborhoodSave)
        }
    }

    fun clearPreferredNeighborhoodErrors() {
        _neighborhoodFormError.update { null }
    }

    override fun onCleared() {
        _uiEvents.close()

        super.onCleared()
    }

    sealed interface UiEvent {
        data class ScrollToField(val field: AtendimentoStep1Field) : UiEvent

        data object ValidateSuccess : UiEvent

        data object FinishSearchedNeighborhoodSave : UiEvent
    }
}
