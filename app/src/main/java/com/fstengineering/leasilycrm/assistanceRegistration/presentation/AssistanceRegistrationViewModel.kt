package com.fstengineering.leasilycrm.assistanceRegistration.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.assistanceRegistration.application.models.commands.AssistanceCommandModel
import com.fstengineering.leasilycrm.assistanceRegistration.application.models.dtos.AssistanceReadModel
import com.fstengineering.leasilycrm.assistanceRegistration.application.models.queries.AssistanceArgs
import com.fstengineering.leasilycrm.assistanceRegistration.application.repositories.AssistanceQueryRepository
import com.fstengineering.leasilycrm.assistanceRegistration.presentation.mappers.toCommandModel
import com.fstengineering.leasilycrm.assistanceRegistration.presentation.mappers.toUiState
import com.fstengineering.leasilycrm.assistanceRegistration.presentation.models.AssistanceDraftFormUiState
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.CommandUseCase
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.QueryUseCase
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.ValidationErrors
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AssistanceRegistrationViewModel(
    private val readAssistance: QueryUseCase<AssistanceArgs, AssistanceReadModel>,
    private val writeAssistance: CommandUseCase<AssistanceCommandModel>,
    private val assistanceQueryRepository: AssistanceQueryRepository,
) : ViewModel() {
    private var isInitialized = false

    var assistanceId: Long? = null
        private set

    private val _uiEvents = Channel<UiEvent>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _pastNeighborhoods =
        MutableStateFlow<ImmutableList<String>>(persistentListOf())
    val pastNeighborhoods = _pastNeighborhoods.asStateFlow()

    private val _draftFormUiState =
        MutableStateFlow<AssistanceDraftFormUiState>(AssistanceDraftFormUiState.Loading)
    val draftFormUiState = _draftFormUiState.asStateFlow()

    private var formToEdit: AssistanceDraftFormUiState? = null

    fun init(
        leadTenantId: Long,
        assistanceId: Long?,
    ) {
        if (isInitialized) return

        this.assistanceId = assistanceId

        readAssistance(leadTenantId, assistanceId)

        getPastNeighborhoods()

        isInitialized = true
    }

    private fun readAssistance(
        leadTenantId: Long,
        assistanceId: Long?,
    ) {
        val query = AssistanceArgs(
            leadTenantId = leadTenantId,
            assistanceId = assistanceId,
        )

        viewModelScope.launch {
            readAssistance.invoke(query)
                .onError { e -> _uiEvents.trySend(UiEvent.BottomSheetError(e)) }
                .onSuccess { storedAssistance ->
                    val draftForm = storedAssistance.toUiState()

                    _draftFormUiState.update { draftForm }
                    formToEdit = draftForm
                }
        }
    }

    private fun getPastNeighborhoods() {
        viewModelScope.launch {
            assistanceQueryRepository.getAllNeighborhoodNames()
                .onError { e -> _uiEvents.trySend(UiEvent.BottomSheetError(e)) }
                .onSuccess { neighborhoodNames -> _pastNeighborhoods.update { neighborhoodNames } }
        }
    }

    fun onAssistanceFormChange(form: AssistanceDraftFormUiState) {
        _draftFormUiState.update { form }
    }

    fun saveAssistance() {
        val form = (draftFormUiState.value as? AssistanceDraftFormUiState.Loaded) ?: return

        viewModelScope.launch {
            if (formToEdit != null && form == formToEdit) {
                _uiEvents.trySend(UiEvent.AssistanceEditSuccess(messageId = R.string.no_data_changed_success_message))
                return@launch
            }

            writeAssistance(command = form.toCommandModel())
                .onError { error ->
                    when (error) {
                        is ApplicationError -> _uiEvents.trySend(UiEvent.BottomSheetError(error))

                        // TODO: Show something in the UI with the Domain errors
                        is ValidationErrors, is DomainError -> {
                            _uiEvents.trySend(UiEvent.BottomSheetError(ApplicationError.Unknown))
                        }
                    }

                    return@launch
                }

            val saveEvent =
                if (formToEdit != null) UiEvent.AssistanceEditSuccess(messageId = R.string.edit_assistance_success_message)
                else UiEvent.AssistanceCreateSuccess

            _uiEvents.trySend(saveEvent)
        }
    }

    fun onExit() {
        val needConfirmation = formToEdit != draftFormUiState.value

        _uiEvents.trySend(UiEvent.Exit(needConfirmation))
    }

    override fun onCleared() {
        _uiEvents.close()

        super.onCleared()
    }

    sealed interface UiEvent {
        data class BottomSheetError(val error: ApplicationError) : UiEvent

        data object AssistanceCreateSuccess : UiEvent

        data class AssistanceEditSuccess(val messageId: Int) : UiEvent

        data object ConvertOldTenantToLead : UiEvent

        data class Exit(val needConfirmation: Boolean) : UiEvent
    }
}
