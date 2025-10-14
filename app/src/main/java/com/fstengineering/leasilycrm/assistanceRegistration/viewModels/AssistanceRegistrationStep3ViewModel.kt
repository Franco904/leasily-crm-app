package com.fstengineering.leasilycrm.assistanceRegistration.viewModels

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.assistanceRegistration.models.AssistanceInteractionErrorUiState
import com.fstengineering.leasilycrm.assistanceRegistration.models.fields.AssistanceInteractionField
import com.fstengineering.leasilycrm.core.domain.models.Assistance
import com.fstengineering.leasilycrm.core.domain.models.AssistanceInteraction
import com.fstengineering.leasilycrm.core.domain.utils.ValidationError
import com.fstengineering.leasilycrm.core.domain.utils.onErrors
import com.fstengineering.leasilycrm.core.domain.validators.interfaces.AssistanceInteractionValidator
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toUiMessage
import kotlinx.collections.immutable.mutate
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AssistanceRegistrationStep3ViewModel(
    private val assistanceInteractionValidator: AssistanceInteractionValidator,
) : ViewModel() {
    private val _uiEvents = Channel<UiEvents>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _interactionForm = MutableStateFlow<AssistanceInteraction?>(null)
    val interactionForm = _interactionForm.asStateFlow()

    var interactionFormBeforeEdit: AssistanceInteraction? = null

    private val _formErrors = MutableStateFlow(AssistanceInteractionErrorUiState())
    val formErrors = _formErrors.asStateFlow()

    fun onInteractionFormChange(
        form: AssistanceInteraction?,
    ) {
        _formErrors.update {
            val newErrors = it.errors.mutate { map ->
                if (interactionForm.value?.interactionDate != form?.interactionDate) {
                    map[AssistanceInteractionField.InteractionDate] = null
                }
                if (interactionForm.value?.title != form?.title) {
                    map[AssistanceInteractionField.Title] = null
                }
            }

            it.copy(errors = newErrors)
        }

        _interactionForm.update { form }
    }

    fun onEditInteraction(interaction: AssistanceInteraction) {
        interactionFormBeforeEdit = interaction
    }

    fun onDismissInteraction() {
        interactionFormBeforeEdit = null
    }

    fun validateInteractionForm(
        assistanceForm: Assistance,
    ) {
        clearErrors()

        viewModelScope.launch {
            val interactionFromForm = interactionForm.value ?: return@launch
            val isEditing = interactionFormBeforeEdit != null

            if (isEditing) {
                val storedInteraction = assistanceForm.interactions
                    .find { it == interactionFormBeforeEdit }

                if (interactionFromForm == storedInteraction) {
                    _uiEvents.send(
                        UiEvents.InteractionEditSuccess(messageId = R.string.snackbar_no_data_changed)
                    )
                    return@launch
                }
            }

            assistanceInteractionValidator.validate(interactionFromForm)
                .onErrors { errors ->
                    val interactionDateError =
                        errors.firstOrNull { it is ValidationError.AssistanceInteractionDate }
                    val titleError =
                        errors.firstOrNull { it is ValidationError.AssistanceInteractionTitle }

                    _formErrors.update {
                        val newErrors = it.errors.mutate { map ->
                            map[AssistanceInteractionField.InteractionDate] =
                                interactionDateError?.toUiMessage()
                            map[AssistanceInteractionField.Title] = titleError?.toUiMessage()
                        }

                        it.copy(errors = newErrors)
                    }

                    return@launch
                }

            val saveEvent =
                if (isEditing) UiEvents.InteractionEditSuccess(messageId = R.string.snackbar_edit_assistance_interaction_succeeded)
                else UiEvents.InteractionCreateSuccess

            _uiEvents.send(saveEvent)
        }
    }

    fun clearErrors() {
        _formErrors.update { AssistanceInteractionErrorUiState() }
    }

    sealed interface UiEvents {
        data object InteractionCreateSuccess : UiEvents

        data class InteractionEditSuccess(@StringRes val messageId: Int) : UiEvents
    }
}
