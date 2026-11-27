package com.fstengineering.leasilycrm.atendimentoRegistration.presentation

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.Destinations
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.atendimentoRegistration.infra.dataSources.AtendimentoRegistrationReadDataSource
import com.fstengineering.leasilycrm.atendimentoRegistration.infra.dataSources.AtendimentoWriteDataSource
import com.fstengineering.leasilycrm.atendimentoRegistration.presentation.mappers.toAtendimento
import com.fstengineering.leasilycrm.atendimentoRegistration.presentation.mappers.toAtendimentoDraftFormUiState
import com.fstengineering.leasilycrm.atendimentoRegistration.presentation.models.AtendimentoDraftFormUiState
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.BaseError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.infra.dataSources.database.ClienteSharedReadDataSource
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AtendimentoRegistrationViewModel(
    private val route: Destinations.AtendimentoRegistration,
    private val atendimentoRegistrationReadDataSource: AtendimentoRegistrationReadDataSource,
    private val clienteSharedReadDataSource: ClienteSharedReadDataSource,
    private val atendimentoWriteDataSource: AtendimentoWriteDataSource,
) : ViewModel() {
    private var isLoaded = false

    val isEditing: Boolean = route.atendimentoId != null

    private val _uiEvents = Channel<UiEvent>(Channel.BUFFERED)
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _pastNeighborhoods =
        MutableStateFlow<ImmutableList<String>>(persistentListOf())
    val pastNeighborhoods = _pastNeighborhoods.asStateFlow()

    private val _draftFormUiState =
        MutableStateFlow<AtendimentoDraftFormUiState>(AtendimentoDraftFormUiState.Loading)
    val draftFormUiState = _draftFormUiState.asStateFlow()

    private var formToEdit: AtendimentoDraftFormUiState? = null

    fun init() {
        if (isLoaded) return
        isLoaded = true

        val atendimentoId = route.atendimentoId
        if (atendimentoId != null) loadAtendimentoToEdit(atendimentoId)
        else loadNewAtendimento()

        loadPastNeighborhoods()
    }

    private fun loadAtendimentoToEdit(atendimentoId: Long) {
        viewModelScope.launch {
            atendimentoRegistrationReadDataSource.getAtendimento(atendimentoId)
                .onError { e -> _uiEvents.trySend(UiEvent.ShowErrorBottomSheet(e)) }
                .onSuccess { storedAtendimento ->
                    val draftForm = storedAtendimento.toAtendimentoDraftFormUiState()

                    _draftFormUiState.update { draftForm }
                    formToEdit = draftForm
                }
        }
    }

    private fun loadNewAtendimento() {
        viewModelScope.launch {
            clienteSharedReadDataSource.getName(route.clienteId)
                .onError { e -> _uiEvents.trySend(UiEvent.ShowErrorBottomSheet(e)) }
                .onSuccess { clienteName ->
                    _draftFormUiState.update {
                        AtendimentoDraftFormUiState.Loaded.empty(route.clienteId, clienteName)
                    }
                }
        }
    }

    private fun loadPastNeighborhoods() {
        viewModelScope.launch {
            atendimentoRegistrationReadDataSource.getNeighborhoodNames()
                .onError { e -> _uiEvents.trySend(UiEvent.ShowErrorBottomSheet(e)) }
                .onSuccess { names -> _pastNeighborhoods.update { names.toPersistentList() } }
        }
    }

    fun onAtendimentoFormChange(form: AtendimentoDraftFormUiState) {
        _draftFormUiState.update { form }
    }

    fun saveAtendimento() {
        val form = (draftFormUiState.value as? AtendimentoDraftFormUiState.Loaded) ?: return

        viewModelScope.launch {
            if (formToEdit != null && form == formToEdit) {
                _uiEvents.trySend(UiEvent.CloseScreenWithSnackbar(R.string.no_data_changed_success_message))
                return@launch
            }

            val atendimento = form.toAtendimento()
                .fold(
                    onError = {
                        _uiEvents.trySend(UiEvent.ShowErrorBottomSheet(ApplicationError.Unknown))

                        return@launch
                    },
                    onSuccess = { it },
                )

            atendimentoWriteDataSource.save(atendimento)
                .onError { error ->
                    _uiEvents.trySend(UiEvent.ShowErrorBottomSheet(error))

                    return@launch
                }

            val messageId =
                if (formToEdit != null) R.string.edit_assistance_success_message
                else R.string.create_assistance_success_message

            _uiEvents.trySend(UiEvent.CloseScreenWithSnackbar(messageId))
        }
    }

    fun onExit() {
        val needConfirmation = when (val form = draftFormUiState.value) {
            is AtendimentoDraftFormUiState.Loading -> false
            is AtendimentoDraftFormUiState.Loaded -> {
                if (formToEdit != null) formToEdit != form
                else form != AtendimentoDraftFormUiState.Loaded.empty(
                    clienteId = form.clienteId,
                    clienteName = form.clienteName,
                )
            }
        }

        _uiEvents.trySend(UiEvent.Exit(needConfirmation))
    }

    sealed interface UiEvent {
        data class ShowErrorBottomSheet(val error: BaseError) : UiEvent

        data class CloseScreenWithSnackbar(@StringRes val messageId: Int) : UiEvent

        data class Exit(val needConfirmation: Boolean) : UiEvent
    }
}
