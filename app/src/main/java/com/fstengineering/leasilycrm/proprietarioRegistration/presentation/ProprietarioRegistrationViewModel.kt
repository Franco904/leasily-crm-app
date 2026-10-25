package com.fstengineering.leasilycrm.proprietarioRegistration.presentation

import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.Destinations
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.ValidationErrors
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.infra.dataSources.sharedStorage.SharedStorageDataSource
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toInlineMessage
import com.fstengineering.leasilycrm.proprietarioRegistration.application.useCases.WriteProprietarioUseCase
import com.fstengineering.leasilycrm.proprietarioRegistration.infra.dataSources.ProprietarioRegistrationReadDataSource
import com.fstengineering.leasilycrm.proprietarioRegistration.presentation.mappers.toCommandModel
import com.fstengineering.leasilycrm.proprietarioRegistration.presentation.mappers.toUiState
import com.fstengineering.leasilycrm.proprietarioRegistration.presentation.models.ProprietarioDraftFormUiState
import com.fstengineering.leasilycrm.proprietarioRegistration.presentation.models.ProprietarioErrorUiState
import com.fstengineering.leasilycrm.proprietarioRegistration.presentation.models.ProprietarioField
import com.fstengineering.leasilycrm.proprietarioRegistration.presentation.models.ProprietarioFileUiState
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.collections.immutable.mutate
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProprietarioRegistrationViewModel(
    private val route: Destinations.ProprietarioRegistration,
    private val writeProprietario: WriteProprietarioUseCase,
    private val proprietarioRegistrationReadDataSource: ProprietarioRegistrationReadDataSource,
    private val sharedStorageDataSource: SharedStorageDataSource,
) : ViewModel() {
    val isEditing: Boolean = route.proprietarioId != null

    private var isLoaded = false

    private val _uiEvents = Channel<UiEvent>(Channel.BUFFERED)
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _draftFormUiState =
        MutableStateFlow<ProprietarioDraftFormUiState>(ProprietarioDraftFormUiState.Loading)
    val draftFormUiState = _draftFormUiState.asStateFlow()

    private val _formErrors = MutableStateFlow(ProprietarioErrorUiState())
    val formErrors = _formErrors.asStateFlow()

    private var formToEdit: ProprietarioDraftFormUiState? = null

    fun init() {
        if (isLoaded) return
        isLoaded = true

        val proprietarioId = route.proprietarioId
        if (proprietarioId == null) {
            _draftFormUiState.update { ProprietarioDraftFormUiState.Loaded.Empty }
            return
        }

        viewModelScope.launch {
            proprietarioRegistrationReadDataSource.getProprietario(proprietarioId)
                .onError { e -> _uiEvents.trySend(UiEvent.BottomSheetError(e)) }
                .onSuccess { readModel ->
                    val draftForm = readModel.toUiState()

                    _draftFormUiState.update { draftForm }
                    formToEdit = draftForm
                }
        }
    }

    fun onProprietarioFormChange(newForm: ProprietarioDraftFormUiState.Loaded) {
        val form = (draftFormUiState.value as? ProprietarioDraftFormUiState.Loaded) ?: return

        _formErrors.update {
            val newErrors = it.errors.mutate { map ->
                if (form.name != newForm.name) map[ProprietarioField.Name] = null
                if (form.phone != newForm.phone) map[ProprietarioField.Phone] = null
                if (form.email != newForm.email) map[ProprietarioField.Email] = null
                if (form.paymentKey != newForm.paymentKey) map[ProprietarioField.PaymentKey] = null
            }

            it.copy(errors = newErrors)
        }

        _draftFormUiState.update { newForm }
    }

    fun onProprietarioFilePick(
        contentUriString: String,
        defaultFileName: String,
    ) {
        val form = (draftFormUiState.value as? ProprietarioDraftFormUiState.Loaded) ?: return

        viewModelScope.launch {
            val fileName = sharedStorageDataSource.getFileName(contentUriString)

            val fileDraft = ProprietarioFileUiState(
                uri = contentUriString.toUri(),
                name = fileName ?: defaultFileName,
            )

            onProprietarioFormChange(newForm = form.addFile(fileDraft))
        }
    }

    fun saveProprietario() {
        val form = (draftFormUiState.value as? ProprietarioDraftFormUiState.Loaded) ?: return

        if (formToEdit != null && form == formToEdit) {
            _uiEvents.trySend(UiEvent.ProprietarioEditSuccess(messageId = R.string.no_data_changed_success_message))
            return
        }

        viewModelScope.launch {
            writeProprietario(form.toCommandModel())
                .onError { error ->
                    when (error) {
                        is ApplicationError -> _uiEvents.trySend(UiEvent.BottomSheetError(error))
                        is ValidationErrors -> setFieldErrors(error.errors)
                        is DomainError -> setFieldErrors(listOf(error))
                    }

                    return@launch
                }

            val saveEvent =
                if (formToEdit != null) UiEvent.ProprietarioEditSuccess(messageId = R.string.edit_property_owner_success_message)
                else UiEvent.ProprietarioCreateSuccess

            _uiEvents.trySend(saveEvent)
        }
    }

    private suspend fun setFieldErrors(errors: List<DomainError>) {
        _formErrors.update { ProprietarioErrorUiState() }
        delay(150.milliseconds)

        val nameError = errors.firstOrNull { it is DomainError.Name }
        val phoneError = errors.firstOrNull { it is DomainError.Phone }
        val emailError = errors.firstOrNull { it is DomainError.Email }
        val paymentKeyError = errors.firstOrNull { it is DomainError.PaymentKey }

        _formErrors.update {
            val newErrors = it.errors.mutate { map ->
                map[ProprietarioField.Name] = nameError?.toInlineMessage()
                map[ProprietarioField.Phone] = phoneError?.toInlineMessage()
                map[ProprietarioField.Email] = emailError?.toInlineMessage()
                map[ProprietarioField.PaymentKey] = paymentKeyError?.toInlineMessage()
            }

            it.copy(errors = newErrors)
        }

        val firstFieldWithError = _formErrors.value.errors.entries.first { it.value != null }.key
        _uiEvents.trySend(UiEvent.ScrollToField(firstFieldWithError))
    }

    fun onExit() {
        val form = (draftFormUiState.value as? ProprietarioDraftFormUiState.Loaded) ?: return

        val needConfirmation = when {
            formToEdit != null -> formToEdit != form
            else -> form.isAnyFormFieldInitialized
        }

        _uiEvents.trySend(UiEvent.Exit(needConfirmation))
    }

    override fun onCleared() {
        _uiEvents.close()

        super.onCleared()
    }

    sealed interface UiEvent {
        data class BottomSheetError(val error: ApplicationError) : UiEvent

        data class ScrollToField(val field: ProprietarioField) : UiEvent

        data object ProprietarioCreateSuccess : UiEvent

        data class ProprietarioEditSuccess(val messageId: Int) : UiEvent

        data class Exit(val needConfirmation: Boolean) : UiEvent
    }
}
