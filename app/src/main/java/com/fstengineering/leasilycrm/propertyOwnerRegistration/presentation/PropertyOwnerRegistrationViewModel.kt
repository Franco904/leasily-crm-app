package com.fstengineering.leasilycrm.propertyOwnerRegistration.presentation

import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.application.services.interfaces.FileStorageService
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.presentation.utils.onApplicationError
import com.fstengineering.leasilycrm.core.presentation.utils.onDomainErrors
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toInlineMessage
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.CommandUseCase
import com.fstengineering.leasilycrm.propertyOwnerRegistration.application.models.commands.PropertyOwnerCommandModel
import com.fstengineering.leasilycrm.propertyOwnerRegistration.application.repositories.PropertyOwnerQueryRepository
import com.fstengineering.leasilycrm.propertyOwnerRegistration.presentation.mappers.toCommandModel
import com.fstengineering.leasilycrm.propertyOwnerRegistration.presentation.mappers.toUiState
import com.fstengineering.leasilycrm.propertyOwnerRegistration.presentation.models.PropertyOwnerDraftFormUiState
import com.fstengineering.leasilycrm.propertyOwnerRegistration.presentation.models.PropertyOwnerErrorUiState
import com.fstengineering.leasilycrm.propertyOwnerRegistration.presentation.models.PropertyOwnerField
import com.fstengineering.leasilycrm.propertyOwnerRegistration.presentation.models.PropertyOwnerFileUiState
import kotlinx.collections.immutable.mutate
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class PropertyOwnerRegistrationViewModel(
    private val writePropertyOwner: CommandUseCase<PropertyOwnerCommandModel>,
    private val propertyOwnerQueryRepository: PropertyOwnerQueryRepository,
    private val fileStorageService: FileStorageService,
) : ViewModel() {
    private var isInitialized = false

    private val _uiEvents = Channel<UiEvent>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _draftFormUiState =
        MutableStateFlow<PropertyOwnerDraftFormUiState>(PropertyOwnerDraftFormUiState.Loading)
    val draftFormUiState = _draftFormUiState.asStateFlow()

    private val _formErrors = MutableStateFlow(PropertyOwnerErrorUiState())
    val formErrors = _formErrors.asStateFlow()

    private var formToEdit: PropertyOwnerDraftFormUiState? = null

    fun init(propertyOwnerId: Long?) {
        if (isInitialized) return

        if (propertyOwnerId != null) {
            getPropertyOwnerToEdit(propertyOwnerId)
        }

        isInitialized = true
    }

    private fun getPropertyOwnerToEdit(propertyOwnerId: Long) {
        viewModelScope.launch {
            propertyOwnerQueryRepository.getById(propertyOwnerId)
                .onError { e -> _uiEvents.trySend(UiEvent.BottomSheetError(e)) }
                .onSuccess { storedOwner ->
                    val draftForm = storedOwner.toUiState()

                    _draftFormUiState.update { draftForm }
                    formToEdit = draftForm
                }
        }
    }

    fun onPropertyOwnerFormChange(newForm: PropertyOwnerDraftFormUiState.Loaded) {
        val form = (draftFormUiState.value as? PropertyOwnerDraftFormUiState.Loaded) ?: return

        _formErrors.update {
            val newErrors = it.errors.mutate { map ->
                if (form.name != newForm.name) map[PropertyOwnerField.Name] = null
                if (form.phone != newForm.phone) map[PropertyOwnerField.Phone] = null
                if (form.email != newForm.email) map[PropertyOwnerField.Email] = null
                if (form.paymentKey != newForm.paymentKey) map[PropertyOwnerField.PaymentKey] = null
            }

            it.copy(errors = newErrors)
        }

        _draftFormUiState.update { newForm }
    }

    fun onPropertyOwnerFilePick(
        contentUriString: String,
        defaultFileName: String,
    ) {
        val form = (draftFormUiState.value as? PropertyOwnerDraftFormUiState.Loaded) ?: return

        viewModelScope.launch {
            val fileName = fileStorageService.getFileName(contentUriString)
                .fold(
                    onError = { e ->
                        _uiEvents.trySend(UiEvent.BottomSheetError(e))
                        return@launch
                    },
                    onSuccess = { fileName -> fileName },
                )

            val fileDraft = PropertyOwnerFileUiState(
                uri = contentUriString.toUri(),
                name = fileName ?: defaultFileName,
            )

            onPropertyOwnerFormChange(newForm = form.addFile(fileDraft))
        }
    }

    fun savePropertyOwner() {
        val form = (draftFormUiState.value as? PropertyOwnerDraftFormUiState.Loaded) ?: return

        if (formToEdit != null && form == formToEdit) {
            _uiEvents.trySend(UiEvent.PropertyOwnerEditSuccess(messageId = R.string.no_data_changed_success_message))
            return
        }

        viewModelScope.launch {
            writePropertyOwner(form.toCommandModel())
                .onApplicationError {
                    _uiEvents.trySend(UiEvent.BottomSheetError(it))
                    return@launch
                }
                .onDomainErrors { errors ->
                    setFieldErrors(errors)
                    return@launch
                }

            val saveEvent =
                if (formToEdit != null) UiEvent.PropertyOwnerEditSuccess(messageId = R.string.edit_property_owner_success_message)
                else UiEvent.PropertyOwnerCreateSuccess

            _uiEvents.trySend(saveEvent)
        }
    }

    private suspend fun setFieldErrors(errors: List<DomainError>) {
        _formErrors.update { PropertyOwnerErrorUiState() }
        delay(150.milliseconds)

        val nameError = errors.firstOrNull { it is DomainError.Name }
        val phoneError = errors.firstOrNull { it is DomainError.Phone }
        val emailError = errors.firstOrNull { it is DomainError.Email }
        val paymentKeyError = errors.firstOrNull { it is DomainError.PaymentKey }

        _formErrors.update {
            val newErrors = it.errors.mutate { map ->
                map[PropertyOwnerField.Name] = nameError?.toInlineMessage()
                map[PropertyOwnerField.Phone] = phoneError?.toInlineMessage()
                map[PropertyOwnerField.Email] = emailError?.toInlineMessage()
                map[PropertyOwnerField.PaymentKey] = paymentKeyError?.toInlineMessage()
            }

            it.copy(errors = newErrors)
        }

        val firstFieldWithError = _formErrors.value.errors.entries.first { it.value != null }.key
        _uiEvents.trySend(UiEvent.ScrollToField(firstFieldWithError))
    }

    fun onExit() {
        val form = (draftFormUiState.value as? PropertyOwnerDraftFormUiState.Loaded) ?: return

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

        data class ScrollToField(val field: PropertyOwnerField) : UiEvent

        data object PropertyOwnerCreateSuccess : UiEvent

        data class PropertyOwnerEditSuccess(val messageId: Int) : UiEvent

        data class Exit(val needConfirmation: Boolean) : UiEvent
    }
}
