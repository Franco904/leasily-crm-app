package com.fstengineering.leasilycrm.atendimentoRegistration.presentation.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.atendimentoRegistration.domain.entities.Interacao
import com.fstengineering.leasilycrm.atendimentoRegistration.presentation.models.AtendimentoDraftFormUiState
import com.fstengineering.leasilycrm.atendimentoRegistration.presentation.models.InteracaoDraftFormUiState
import com.fstengineering.leasilycrm.atendimentoRegistration.presentation.models.InteracaoErrorUiState
import com.fstengineering.leasilycrm.atendimentoRegistration.presentation.models.fields.InteracaoField
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.toDomainErrors
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toInlineMessage
import kotlinx.collections.immutable.mutate
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AtendimentoRegistrationStep3ViewModel : ViewModel() {
    private val _uiEvents = Channel<UiEvent>(Channel.BUFFERED)
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _interactionForm = MutableStateFlow<InteracaoDraftFormUiState?>(null)
    val interacaoForm = _interactionForm.asStateFlow()

    var interacaoFormBeforeEdit: InteracaoDraftFormUiState.Loaded? = null

    private val _formErrors = MutableStateFlow(InteracaoErrorUiState())
    val formErrors = _formErrors.asStateFlow()

    fun onOpenInteracaoForm() {
        _interactionForm.update { InteracaoDraftFormUiState.Loading }
    }

    fun onInteracaoFormChange(newForm: InteracaoDraftFormUiState.Loaded) {
        val form = interacaoForm.value as? InteracaoDraftFormUiState.Loaded ?: return

        _formErrors.update {
            val newErrors = it.errors.mutate { map ->
                if (form.date != newForm.date) {
                    map[InteracaoField.Date] = null
                }
                if (form.title != newForm.title) {
                    map[InteracaoField.Title] = null
                }
            }

            it.copy(errors = newErrors)
        }

        _interactionForm.update { newForm }
    }

    fun clearInteracaoForm() {
        _interactionForm.update { null }
        _formErrors.update { InteracaoErrorUiState() }
        interacaoFormBeforeEdit = null
    }

    fun onEditInteracao(interacao: InteracaoDraftFormUiState.Loaded) {
        interacaoFormBeforeEdit = interacao
    }

    fun validateInteracaoForm(atendimentoForm: AtendimentoDraftFormUiState.Loaded) {
        clearErrors()

        val interacaoFromForm =
            interacaoForm.value as? InteracaoDraftFormUiState.Loaded ?: return

        val isEditing = interacaoFormBeforeEdit != null

        if (isEditing) {
            val storedInteracao = atendimentoForm.interacoes
                .find { it == interacaoFormBeforeEdit }

            if (interacaoFromForm == storedInteracao) {
                val msg = R.string.no_data_changed_success_message
                _uiEvents.trySend(UiEvent.FinishInteracaoEdit(messageId = msg))
                return
            }
        }

        viewModelScope.launch {
            val parsingResults = Interacao.parse(
                id = interacaoFromForm.id,
                date = interacaoFromForm.date,
                title = interacaoFromForm.title,
                description = interacaoFromForm.description,
            )

            parsingResults
                .onError { error ->
                    val errors = error.toDomainErrors()
                    val dateError =
                        errors.firstOrNull { it is DomainError.InteracaoDate }
                    val titleError =
                        errors.firstOrNull { it is DomainError.InteracaoTitle }

                    _formErrors.update {
                        val newErrors = it.errors.mutate { map ->
                            map[InteracaoField.Date] =
                                dateError?.toInlineMessage()
                            map[InteracaoField.Title] = titleError?.toInlineMessage()
                        }

                        it.copy(errors = newErrors)
                    }

                    return@launch
                }

            val saveEvent =
                if (isEditing) UiEvent.FinishInteracaoEdit(messageId = R.string.edit_assistance_interaction_success_message)
                else UiEvent.FinishInteracaoCreate

            _uiEvents.trySend(saveEvent)
        }
    }

    private fun clearErrors() {
        _formErrors.update { InteracaoErrorUiState() }
    }

    override fun onCleared() {
        _uiEvents.close()

        super.onCleared()
    }

    sealed interface UiEvent {
        data object FinishInteracaoCreate : UiEvent

        data class FinishInteracaoEdit(val messageId: Int) : UiEvent
    }
}
