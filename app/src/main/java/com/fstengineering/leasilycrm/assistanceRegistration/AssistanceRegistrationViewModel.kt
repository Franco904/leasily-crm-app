package com.fstengineering.leasilycrm.assistanceRegistration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.assistanceRegistration.application.models.AssistanceQueryModel
import com.fstengineering.leasilycrm.assistanceRegistration.application.useCases.ReadAssistanceUseCase
import com.fstengineering.leasilycrm.assistanceRegistration.application.useCases.WriteAssistanceUseCase
import com.fstengineering.leasilycrm.assistanceRegistration.presentation.models.AssistanceDraftFormUiState
import com.fstengineering.leasilycrm.core.application.repositories.AssistanceRepository
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onErrors
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
    private val assistanceRepository: AssistanceRepository,
    private val readAssistance: ReadAssistanceUseCase,
    private val writeAssistance: WriteAssistanceUseCase,
) : ViewModel() {
    private var isInitialized = false

    var assistanceId: Long? = null
        private set

    private val _uiEvents = Channel<UiEvents>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _pastNeighborhoods =
        MutableStateFlow<ImmutableList<String>>(persistentListOf())
    val pastNeighborhoods = _pastNeighborhoods.asStateFlow()

    private val _draftFormUiState =
        MutableStateFlow<AssistanceDraftFormUiState>(AssistanceDraftFormUiState.Loading)
    val draftForm = _draftFormUiState.asStateFlow()

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
        viewModelScope.launch {
            val query = AssistanceQueryModel(
                leadTenantId = leadTenantId,
                assistanceId = assistanceId,
            )

            readAssistance.invoke(query)
                .onError { e -> _uiEvents.trySend(UiEvents.BottomSheetError(e)) }
                .onSuccess { storedAssistance ->
                    val draftForm =
                        AssistanceDraftFormUiState.Loaded.fromReadModel(storedAssistance)

                    _draftFormUiState.update { draftForm }
                    formToEdit = draftForm
                }
        }
    }

    private fun getPastNeighborhoods() {
        viewModelScope.launch {
            assistanceRepository.getAllNeighborhoodNames()
                .onError { e -> _uiEvents.trySend(UiEvents.BottomSheetError(e)) }
                .onSuccess { neighborhoodNames -> _pastNeighborhoods.update { neighborhoodNames } }
        }
    }

    fun onAssistanceFormChange(form: AssistanceDraftFormUiState) {
        _draftFormUiState.update { form }
    }

    fun saveAssistance() {
        val form = draftForm.value
        if (form !is AssistanceDraftFormUiState.Loaded) return

        viewModelScope.launch {
            if (formToEdit != null && form == formToEdit) {
                _uiEvents.trySend(UiEvents.AssistanceEditSuccess(messageId = R.string.no_data_changed_success_message))
                return@launch
            }

            writeAssistance(command = form.toCommand())
                .onErrors { errors ->
                    errors.filterIsInstance<ApplicationError>()
                        .firstOrNull()
                        ?.let { _uiEvents.send(UiEvents.BottomSheetError(it)) }

                    val hasDomainError = errors.any { it is DomainError }
                    if (hasDomainError) {
                        _draftFormUiState.update { form.copy(hasDomainError = true) }
                    }
                }

            val saveEvent =
                if (formToEdit != null) UiEvents.AssistanceEditSuccess(messageId = R.string.edit_assistance_success_message)
                else UiEvents.AssistanceCreateSuccess

            _uiEvents.trySend(saveEvent)
        }
    }

    fun onExit() {
        val needConfirmation = formToEdit != draftForm.value

        viewModelScope.launch {
            _uiEvents.trySend(UiEvents.Exit(needConfirmation))
        }
    }

    override fun onCleared() {
        _uiEvents.close()

        super.onCleared()
    }

    sealed interface UiEvents {
        data class BottomSheetError(val error: ApplicationError) : UiEvents

        data object AssistanceCreateSuccess : UiEvents

        data class AssistanceEditSuccess(val messageId: Int) : UiEvents

        data object ConvertOldTenantToLead : UiEvents

        data class Exit(val needConfirmation: Boolean) : UiEvents
    }
}
