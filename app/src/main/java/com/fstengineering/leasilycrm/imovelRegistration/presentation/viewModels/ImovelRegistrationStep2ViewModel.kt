package com.fstengineering.leasilycrm.imovelRegistration.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.toDomainErrors
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toInlineMessage
import com.fstengineering.leasilycrm.imovelRegistration.domain.entities.Imovel
import com.fstengineering.leasilycrm.imovelRegistration.presentation.models.ImovelDraftFormUiState
import com.fstengineering.leasilycrm.imovelRegistration.presentation.models.ImovelStep2ErrorUiState
import com.fstengineering.leasilycrm.imovelRegistration.presentation.models.fields.ImovelStep2Field
import kotlinx.collections.immutable.mutate
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class ImovelRegistrationStep2ViewModel : ViewModel() {
    private val _uiEvents = Channel<UiEvent>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _formErrors = MutableStateFlow(ImovelStep2ErrorUiState())
    val formErrors = _formErrors.asStateFlow()

    fun onImovelFormChange(
        currentForm: ImovelDraftFormUiState.Loaded,
        newForm: ImovelDraftFormUiState.Loaded,
    ) {
        _formErrors.update {
            val newErrors = it.errors.mutate { map ->
                if (newForm.distanceToSea != currentForm.distanceToSea) {
                    map[ImovelStep2Field.DistanceToSea] = null
                }
            }

            it.copy(errors = newErrors)
        }
    }

    fun validateImovelStep2(form: ImovelDraftFormUiState.Loaded) {
        Imovel.parse(
            seaProximityTypeId = form.seaProximityTypeId,
            distanceToSea = form.distanceToSea,
        )
            .onError { error -> setFieldErrors(error.toDomainErrors()) }
    }

    private fun setFieldErrors(errors: List<DomainError>) {
        _formErrors.update { ImovelStep2ErrorUiState() }

        viewModelScope.launch {
            delay(150.milliseconds)

            val distanceToSeaError =
                errors.firstOrNull { it is DomainError.ImovelSeaProximityDistanceToSea }

            _formErrors.update {
                val newErrors = it.errors.mutate { map ->
                    map[ImovelStep2Field.DistanceToSea] = distanceToSeaError?.toInlineMessage()
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
        data class ScrollToField(val field: ImovelStep2Field) : UiEvent
        data object ValidateSuccess : UiEvent
    }
}
