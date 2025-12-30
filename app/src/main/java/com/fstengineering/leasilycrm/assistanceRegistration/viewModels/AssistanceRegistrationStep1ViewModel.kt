package com.fstengineering.leasilycrm.assistanceRegistration.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.assistanceRegistration.models.AssistanceSearchedNeighborhoodErrorUiState
import com.fstengineering.leasilycrm.assistanceRegistration.models.AssistanceStep1ErrorUiState
import com.fstengineering.leasilycrm.assistanceRegistration.models.fields.AssistanceNeighborhoodField
import com.fstengineering.leasilycrm.assistanceRegistration.models.fields.AssistanceStep1Field
import com.fstengineering.leasilycrm.core.domain.models.Assistance
import com.fstengineering.leasilycrm.core.domain.models.AssistanceSearchedNeighborhood
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.onErrors
import com.fstengineering.leasilycrm.core.domain.validators.interfaces.AssistanceSearchedNeighborhoodValidator
import com.fstengineering.leasilycrm.core.domain.validators.interfaces.AssistanceValidator
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

class AssistanceRegistrationStep1ViewModel(
    private val assistanceValidator: AssistanceValidator,
    private val assistanceSearchedNeighborhoodValidator: AssistanceSearchedNeighborhoodValidator,
) : ViewModel() {
    private val _uiEvents = Channel<UiEvents>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _formErrors = MutableStateFlow(AssistanceStep1ErrorUiState())
    val formErrors = _formErrors.asStateFlow()

    private val _searchedNeighborhoodForm = MutableStateFlow<AssistanceSearchedNeighborhood?>(null)
    val searchedNeighborhoodForm = _searchedNeighborhoodForm.asStateFlow()

    private val _searchedNeighborhoodErrors =
        MutableStateFlow(AssistanceSearchedNeighborhoodErrorUiState())
    val searchedNeighborhoodErrors = _searchedNeighborhoodErrors.asStateFlow()

    fun onAssistanceFormChange(
        assistanceForm: Assistance,
        form: Assistance,
    ) {
        _formErrors.update {
            val newErrors = it.errors.mutate { map ->
                if (assistanceForm.distanceToSea != form.distanceToSea) {
                    map[AssistanceStep1Field.DistanceToSea] = null
                }
            }

            it.copy(errors = newErrors)
        }
    }

    fun onSearchedNeighborhoodFormChange(
        form: AssistanceSearchedNeighborhood?,
    ) {
        _searchedNeighborhoodForm.update { form }
    }

    fun validateAssistanceStep1(
        assistanceForm: Assistance,
    ) {
        viewModelScope.launch {
            assistanceValidator.validate(assistance = assistanceForm)
                .onErrors { errors ->
                    setFieldErrors(errors)
                    return@launch
                }

            _uiEvents.send(UiEvents.ValidateSuccess)
        }
    }

    fun saveSearchedNeighborhood() {
        viewModelScope.launch {
            val newSearchedNeighborhood = searchedNeighborhoodForm.value ?: return@launch

            assistanceSearchedNeighborhoodValidator.validate(newSearchedNeighborhood)
                .onErrors { errors ->
                    val nameError =
                        errors.firstOrNull { it is DomainError.AssistanceSearchedNeighborhoodName }

                    _searchedNeighborhoodErrors.update {
                        val newErrors = it.errors.mutate { map ->
                            map[AssistanceNeighborhoodField.Neighborhood] =
                                nameError?.toInlineMessage()
                        }

                        it.copy(errors = newErrors)
                    }

                    return@launch
                }

            clearSearchedNeighborhoodErrors()
            _uiEvents.send(UiEvents.AssistanceSearchedNeighborhoodSaveSuccess)
        }
    }

    fun clearSearchedNeighborhoodErrors() {
        _searchedNeighborhoodErrors.update { AssistanceSearchedNeighborhoodErrorUiState() }
    }

    private suspend fun setFieldErrors(errors: List<DomainError>) {
        _formErrors.update { AssistanceStep1ErrorUiState() }
        delay(150.milliseconds)

        val propertyDistanceToSeaError =
            errors.firstOrNull { it is DomainError.PropertyDistanceToSea }

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
