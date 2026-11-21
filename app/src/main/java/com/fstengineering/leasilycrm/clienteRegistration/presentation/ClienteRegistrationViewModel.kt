package com.fstengineering.leasilycrm.clienteRegistration.presentation

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.Destinations
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.clienteRegistration.infra.dataSources.ClienteRegistrationReadDataSource
import com.fstengineering.leasilycrm.clienteRegistration.infra.dataSources.ClienteWriteDataSource
import com.fstengineering.leasilycrm.clienteRegistration.presentation.mappers.toCliente
import com.fstengineering.leasilycrm.clienteRegistration.presentation.mappers.toClienteDraftFormUiState
import com.fstengineering.leasilycrm.clienteRegistration.presentation.models.ClienteDraftFormUiState
import com.fstengineering.leasilycrm.clienteRegistration.presentation.models.ClienteErrorUiState
import com.fstengineering.leasilycrm.clienteRegistration.presentation.models.ClienteField
import com.fstengineering.leasilycrm.core.domain.utils.toDomainErrors
import com.fstengineering.leasilycrm.core.domain.utils.BaseError
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toInlineMessage
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class ClienteRegistrationViewModel(
    private val route: Destinations.ClienteRegistration,
    private val clienteWriteDataSource: ClienteWriteDataSource,
    private val clienteRegistrationReadDataSource: ClienteRegistrationReadDataSource,
) : ViewModel() {
    val isEditing: Boolean = route.clienteId != null

    private var isLoaded = false

    private val _uiEvents = Channel<UiEvent>(Channel.BUFFERED)
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _draftFormUiState =
        MutableStateFlow<ClienteDraftFormUiState>(ClienteDraftFormUiState.Loading)
    val draftFormUiState: StateFlow<ClienteDraftFormUiState> = _draftFormUiState.asStateFlow()

    private var formToEdit: ClienteDraftFormUiState? = null

    private val _formErrors = MutableStateFlow(ClienteErrorUiState())
    val formErrors = _formErrors.asStateFlow()

    private val _pastTags = MutableStateFlow<ImmutableList<String>>(persistentListOf())
    val pastTags = _pastTags.asStateFlow()

    private val _pastCities = MutableStateFlow<ImmutableList<String>>(persistentListOf())
    val pastCities = _pastCities.asStateFlow()

    private val _pastContactOrigins = MutableStateFlow<ImmutableList<String>>(persistentListOf())
    val pastContactOrigins = _pastContactOrigins.asStateFlow()

    fun init() {
        if (isLoaded) return
        isLoaded = true

        loadPastTags()
        loadPastCities()
        loadPastContactOrigins()

        val clienteId = route.clienteId
        if (clienteId == null) {
            _draftFormUiState.update { ClienteDraftFormUiState.Loaded.Empty }
            return
        }

        loadClienteToEdit(clienteId)
    }

    private fun loadPastTags() {
        viewModelScope.launch {
            clienteRegistrationReadDataSource.getTags()
                .onError { e -> _uiEvents.trySend(UiEvent.ShowErrorBottomSheet(e)) }
                .onSuccess { tags -> _pastTags.update { tags.toPersistentList() } }
        }
    }

    private fun loadPastCities() {
        viewModelScope.launch {
            clienteRegistrationReadDataSource.getCities()
                .onError { e -> _uiEvents.trySend(UiEvent.ShowErrorBottomSheet(e)) }
                .onSuccess { cities -> _pastCities.update { cities.toPersistentList() } }
        }
    }

    private fun loadPastContactOrigins() {
        viewModelScope.launch {
            clienteRegistrationReadDataSource.getContactOrigins()
                .onError { e -> _uiEvents.trySend(UiEvent.ShowErrorBottomSheet(e)) }
                .onSuccess { contactOrigins ->
                    _pastContactOrigins.update { contactOrigins.toPersistentList() }
                }
        }
    }

    private fun loadClienteToEdit(clienteId: Long) {
        viewModelScope.launch {
            clienteRegistrationReadDataSource.getCliente(clienteId)
                .onError { e -> _uiEvents.trySend(UiEvent.ShowErrorBottomSheet(e)) }
                .onSuccess { readModel ->
                    val draftForm = readModel.toClienteDraftFormUiState()

                    _draftFormUiState.update { draftForm }
                    formToEdit = draftForm
                }
        }
    }

    fun onClienteFormChange(newForm: ClienteDraftFormUiState.Loaded) {
        val form = (draftFormUiState.value as? ClienteDraftFormUiState.Loaded) ?: return

        _formErrors.update {
            val newErrors = it.errors.mutate { map ->
                if (form.name != newForm.name) map[ClienteField.Name] = null
                if (form.phone != newForm.phone) map[ClienteField.Phone] = null
                if (form.email != newForm.email) map[ClienteField.Email] = null
            }

            it.copy(errors = newErrors)
        }

        _draftFormUiState.update { newForm }
    }

    fun saveCliente() {
        val form = (draftFormUiState.value as? ClienteDraftFormUiState.Loaded) ?: return

        if (formToEdit != null && form == formToEdit) {
            _uiEvents.trySend(UiEvent.CloseScreenWithSnackbar(messageId = R.string.no_data_changed_success_message))
            return
        }

        viewModelScope.launch {
            val cliente = form.toCliente()
                .fold(onError = { setFieldErrors(it.toDomainErrors()); return@launch }, onSuccess = { it })

            clienteWriteDataSource.save(cliente)
                .onError { error ->
                    _uiEvents.trySend(UiEvent.ShowErrorBottomSheet(error))

                    return@launch
                }

            val messageId = when {
                formToEdit == null -> R.string.create_lead_success_message
                form.isLead -> R.string.edit_lead_success_message
                else -> R.string.edit_tenant_success_message
            }

            _uiEvents.trySend(UiEvent.CloseScreenWithSnackbar(messageId))
        }
    }

    private suspend fun setFieldErrors(errors: List<DomainError>) {
        _formErrors.update { ClienteErrorUiState() }
        delay(150.milliseconds)

        val nameError = errors.firstOrNull { it is DomainError.Name }
        val phoneError = errors.firstOrNull { it is DomainError.Phone }
        val emailError = errors.firstOrNull { it is DomainError.Email }

        _formErrors.update {
            val newErrors = it.errors.mutate { map ->
                map[ClienteField.Name] = nameError?.toInlineMessage()
                map[ClienteField.Phone] = phoneError?.toInlineMessage()
                map[ClienteField.Email] = emailError?.toInlineMessage()
            }

            it.copy(errors = newErrors)
        }

        val firstFieldWithError = _formErrors.value.errors.entries.first { it.value != null }.key
        _uiEvents.trySend(UiEvent.ScrollToField(firstFieldWithError))
    }

    fun onExit() {
        val form = (draftFormUiState.value as? ClienteDraftFormUiState.Loaded) ?: return

        val needConfirmation = when {
            formToEdit != null -> formToEdit != form
            else -> form.isAnyFormFieldInitialized
        }

        _uiEvents.trySend(if (needConfirmation) UiEvent.ShowConfirmLeaveDialog else UiEvent.CloseScreen)
    }

    override fun onCleared() {
        _uiEvents.close()

        super.onCleared()
    }

    sealed interface UiEvent {
        data class ShowErrorBottomSheet(val error: BaseError) : UiEvent

        data class ScrollToField(val field: ClienteField) : UiEvent

        data class CloseScreenWithSnackbar(@param:StringRes val messageId: Int = 1) : UiEvent

        data object ShowConfirmLeaveDialog : UiEvent

        data object CloseScreen : UiEvent
    }
}
