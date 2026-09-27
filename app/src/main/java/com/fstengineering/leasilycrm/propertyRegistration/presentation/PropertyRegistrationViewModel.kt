package com.fstengineering.leasilycrm.propertyRegistration.presentation

import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.CommandUseCase
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.QueryUseCase
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.ValidationErrors
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.presentation.models.PropertyFileUiState
import com.fstengineering.leasilycrm.leadTenantRegistration.presentation.models.LeadTenantDraftFormUiState
import com.fstengineering.leasilycrm.propertyRegistration.application.models.commands.PropertyCommandModel
import com.fstengineering.leasilycrm.propertyRegistration.application.models.dtos.PropertyReadModel
import com.fstengineering.leasilycrm.propertyRegistration.application.models.queries.PropertyArgs
import com.fstengineering.leasilycrm.propertyRegistration.application.repositories.PropertyOwnerQueryRepository
import com.fstengineering.leasilycrm.propertyRegistration.application.repositories.PropertyQueryRepository
import com.fstengineering.leasilycrm.propertyRegistration.presentation.mappers.toCommandModel
import com.fstengineering.leasilycrm.propertyRegistration.presentation.mappers.toUiState
import com.fstengineering.leasilycrm.propertyRegistration.presentation.models.PropertyDraftFormUiState
import com.fstengineering.leasilycrm.propertyRegistration.presentation.models.PropertyOwnerUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PropertyRegistrationViewModel(
    private val readProperty: QueryUseCase<PropertyArgs, PropertyReadModel>,
    private val writeProperty: CommandUseCase<PropertyCommandModel>,
    private val propertyQueryRepository: PropertyQueryRepository,
    private val propertyOwnerQueryRepository: PropertyOwnerQueryRepository,
) : ViewModel() {
    private var isInitialized = false

    var propertyId: Long? = null
        private set

    private val _uiEvents = Channel<UiEvent>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _propertyOwners =
        MutableStateFlow<ImmutableList<PropertyOwnerUiState>>(persistentListOf())
    val propertyOwners = _propertyOwners.asStateFlow()

    private val _pastNeighborhoods =
        MutableStateFlow<ImmutableList<String>>(persistentListOf())
    val pastNeighborhoods = _pastNeighborhoods.asStateFlow()

    private val _draftFormUiState =
        MutableStateFlow<PropertyDraftFormUiState>(PropertyDraftFormUiState.Loading)
    val draftFormUiState = _draftFormUiState.asStateFlow()

    private var formToEdit: PropertyDraftFormUiState? = null

    fun init(propertyId: Long?) {
        if (isInitialized) return

        this.propertyId = propertyId

        if (propertyId != null) getPropertyToEdit(propertyId)
        else _draftFormUiState.update { PropertyDraftFormUiState.Loaded.Empty }

        watchPropertyOwners()
        getPastNeighborhoods()

        isInitialized = true
    }

    private fun getPropertyToEdit(propertyId: Long) {
        val query = PropertyArgs(id = propertyId)

        viewModelScope.launch {
            readProperty(query)
                .onError { e -> _uiEvents.trySend(UiEvent.BottomSheetError(e)) }
                .onSuccess { storedProperty ->
                    val draftForm = storedProperty.toUiState()

                    _draftFormUiState.update { draftForm }
                    formToEdit = draftForm
                }
        }
    }

    private fun watchPropertyOwners() {
        viewModelScope.launch {
            propertyOwnerQueryRepository.watchAll().collectLatest { result ->
                result
                    .onError { e -> _uiEvents.trySend(UiEvent.BottomSheetError(e)) }
                    .onSuccess { storedOwners ->
                        val states = storedOwners.map { it.toUiState() }.toPersistentList()
                        _propertyOwners.update { states }
                    }
            }
        }
    }

    private fun getPastNeighborhoods() {
        viewModelScope.launch {
            propertyQueryRepository.getAllNeighborhoodNames()
                .onError { e -> _uiEvents.trySend(UiEvent.BottomSheetError(e)) }
                .onSuccess { neighborhoodNames -> _pastNeighborhoods.update { neighborhoodNames } }
        }
    }

    fun onPropertyFormChange(form: PropertyDraftFormUiState) {
        _draftFormUiState.update { form }
    }

    fun onPropertyFilePick(contentUriString: String) {
        val form = (draftFormUiState.value as? PropertyDraftFormUiState.Loaded) ?: return

        val fileDraft = PropertyFileUiState(
            uri = contentUriString.toUri(),
            isMainPicture = false,
        )

        onPropertyFormChange(form.addFile(fileDraft))
    }

    fun saveProperty() {
        val form = (draftFormUiState.value as? PropertyDraftFormUiState.Loaded) ?: return

        viewModelScope.launch {
            if (formToEdit != null && form == formToEdit) {
                _uiEvents.trySend(UiEvent.PropertyEditSuccess(messageId = R.string.no_data_changed_success_message))
                return@launch
            }

            writeProperty(form.toCommandModel())
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
                if (formToEdit != null) UiEvent.PropertyEditSuccess(messageId = R.string.edit_property_success_message)
                else UiEvent.PropertyCreateSuccess

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

        data object PropertyCreateSuccess : UiEvent

        data class PropertyEditSuccess(val messageId: Int) : UiEvent

        data class Exit(val needConfirmation: Boolean) : UiEvent
    }
}
