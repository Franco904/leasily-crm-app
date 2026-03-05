package com.fstengineering.leasilycrm.assistanceRegistration.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.assistanceRegistration.presentation.models.AssistanceDraftFormUiState
import com.fstengineering.leasilycrm.assistanceRegistration.presentation.models.AssistanceInteractionDraftFormUiState
import com.fstengineering.leasilycrm.assistanceRegistration.presentation.models.AssistanceInteractionErrorUiState
import com.fstengineering.leasilycrm.assistanceRegistration.presentation.models.fields.AssistanceInteractionField
import com.fstengineering.leasilycrm.core.domain.entities.AssistanceInteraction
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.onErrors
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toInlineMessage
import kotlinx.collections.immutable.mutate
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AssistanceRegistrationStep3ViewModel : ViewModel() {
    private val _uiEvents = Channel<UiEvents>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _interactionForm = MutableStateFlow<AssistanceInteractionDraftFormUiState?>(null)
    val interactionForm = _interactionForm.asStateFlow()

    var interactionFormBeforeEdit: AssistanceInteractionDraftFormUiState.Loaded? = null

    private val _formErrors = MutableStateFlow(AssistanceInteractionErrorUiState())
    val formErrors = _formErrors.asStateFlow()

    fun onOpenInteractionForm() {
        _interactionForm.update { AssistanceInteractionDraftFormUiState.Loading }
    }

    fun onInteractionFormChange(newForm: AssistanceInteractionDraftFormUiState.Loaded) {
        val form = interactionForm.value as? AssistanceInteractionDraftFormUiState.Loaded ?: return

        _formErrors.update {
            val newErrors = it.errors.mutate { map ->
                if (form.date != newForm.date) {
                    map[AssistanceInteractionField.Date] = null
                }
                if (form.title != newForm.title) {
                    map[AssistanceInteractionField.Title] = null
                }
            }

            it.copy(errors = newErrors)
        }

        _interactionForm.update { newForm }
    }

    fun clearInteractionForm() {
        _interactionForm.update { null }
        _formErrors.update { AssistanceInteractionErrorUiState() }
        interactionFormBeforeEdit = null
    }

    fun onEditInteraction(interaction: AssistanceInteractionDraftFormUiState.Loaded) {
        interactionFormBeforeEdit = interaction
    }

    fun validateInteractionForm(assistanceForm: AssistanceDraftFormUiState.Loaded) {
        clearErrors()

        val interactionFromForm =
            interactionForm.value as? AssistanceInteractionDraftFormUiState.Loaded ?: return

        val isEditing = interactionFormBeforeEdit != null

        if (isEditing) {
            val storedInteraction = assistanceForm.interactions
                .find { it == interactionFormBeforeEdit }

            if (interactionFromForm == storedInteraction) {
                val msg = R.string.no_data_changed_success_message
                _uiEvents.trySend(UiEvents.InteractionEditSuccess(messageId = msg))
                return
            }
        }

        viewModelScope.launch {
            val parsingResults = AssistanceInteraction.parse(
                id = interactionFromForm.id,
                date = interactionFromForm.date,
                title = interactionFromForm.title,
                description = interactionFromForm.description,
            )

            parsingResults
                .onErrors { errors ->
                    val dateError =
                        errors.firstOrNull { it is DomainError.AssistanceInteractionDate }
                    val titleError =
                        errors.firstOrNull { it is DomainError.AssistanceInteractionTitle }

                    _formErrors.update {
                        val newErrors = it.errors.mutate { map ->
                            map[AssistanceInteractionField.Date] =
                                dateError?.toInlineMessage()
                            map[AssistanceInteractionField.Title] = titleError?.toInlineMessage()
                        }

                        it.copy(errors = newErrors)
                    }

                    return@launch
                }

            val saveEvent =
                if (isEditing) UiEvents.InteractionEditSuccess(messageId = R.string.edit_assistance_interaction_success_message)
                else UiEvents.InteractionCreateSuccess

            _uiEvents.trySend(saveEvent)
        }
    }

    private fun clearErrors() {
        _formErrors.update { AssistanceInteractionErrorUiState() }
    }

    sealed interface UiEvents {
        data object InteractionCreateSuccess : UiEvents

        data class InteractionEditSuccess(val messageId: Int) : UiEvents
    }
}
