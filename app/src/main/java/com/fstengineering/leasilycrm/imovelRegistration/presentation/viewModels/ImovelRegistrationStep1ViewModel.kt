package com.fstengineering.leasilycrm.imovelRegistration.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.domain.utils.toDomainErrors
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toInlineMessage
import com.fstengineering.leasilycrm.imovelRegistration.domain.entities.Imovel
import com.fstengineering.leasilycrm.imovelRegistration.presentation.models.ImovelDraftFormUiState
import com.fstengineering.leasilycrm.imovelRegistration.presentation.models.ImovelStep1ErrorUiState
import com.fstengineering.leasilycrm.imovelRegistration.presentation.models.fields.ImovelStep1Field
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

class ImovelRegistrationStep1ViewModel : ViewModel() {
    private val _uiEvents = Channel<UiEvent>(Channel.BUFFERED)
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _formErrors = MutableStateFlow(ImovelStep1ErrorUiState())
    val formErrors = _formErrors.asStateFlow()

    fun onImovelFormChange(
        currentForm: ImovelDraftFormUiState.Loaded,
        newForm: ImovelDraftFormUiState.Loaded,
    ) {
        _formErrors.update {
            val newErrors = it.errors.mutate { map ->
                if (newForm.proprietario != currentForm.proprietario) {
                    map[ImovelStep1Field.Proprietario] = null
                }
                if (newForm.type != currentForm.type) {
                    map[ImovelStep1Field.ImovelType] = null
                }
                if (newForm.neighborhood != currentForm.neighborhood) {
                    map[ImovelStep1Field.Neighborhood] = null
                }
                if (newForm.streetAddress != currentForm.streetAddress) {
                    map[ImovelStep1Field.StreetAddress] = null
                }
            }

            it.copy(errors = newErrors)
        }
    }

    fun validateImovelStep1(form: ImovelDraftFormUiState.Loaded) {
        Imovel.parse(
            proprietarioId = form.proprietario?.id,
            type = form.type,
            neighborhood = form.neighborhood,
            streetAddress = form.streetAddress,
        )
            .onSuccess { _uiEvents.trySend(UiEvent.ValidateSuccess) }
            .onError { error -> setFieldErrors(error.toDomainErrors()) }
    }

    private fun setFieldErrors(errors: ImmutableList<DomainError>) {
        _formErrors.update { ImovelStep1ErrorUiState() }

        viewModelScope.launch {
            delay(150.milliseconds)

            val proprietarioError = errors.firstOrNull { it is DomainError.ImovelProprietario }
            val imovelTypeError = errors.firstOrNull { it is DomainError.ImovelType }
            val imovelNeighborhoodError =
                errors.firstOrNull { it is DomainError.Neighborhood }
            val imovelStreetAddressError =
                errors.firstOrNull { it is DomainError.ImovelStreetAddress }

            _formErrors.update {
                val newErrors = it.errors.mutate { map ->
                    map[ImovelStep1Field.Proprietario] =
                        proprietarioError?.toInlineMessage()
                    map[ImovelStep1Field.ImovelType] =
                        imovelTypeError?.toInlineMessage()
                    map[ImovelStep1Field.Neighborhood] =
                        imovelNeighborhoodError?.toInlineMessage()
                    map[ImovelStep1Field.StreetAddress] =
                        imovelStreetAddressError?.toInlineMessage()
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

    sealed interface UiEvent {
        data class ScrollToField(val field: ImovelStep1Field) : UiEvent
        data object ValidateSuccess : UiEvent
    }
}
