package com.fstengineering.leasilycrm.imovelRegistration.presentation

import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.ValidationErrors
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.presentation.models.ImovelFileUiState
import com.fstengineering.leasilycrm.leadTenantRegistration.presentation.models.LeadTenantDraftFormUiState
import com.fstengineering.leasilycrm.imovelRegistration.application.models.queries.ImovelArgs
import com.fstengineering.leasilycrm.imovelRegistration.application.repositories.ImovelQueryRepository
import com.fstengineering.leasilycrm.imovelRegistration.application.useCases.ReadImovelUseCase
import com.fstengineering.leasilycrm.imovelRegistration.infra.dataSources.ImovelWriteDataSource
import com.fstengineering.leasilycrm.imovelRegistration.infra.dataSources.ProprietarioReadDataSource
import com.fstengineering.leasilycrm.imovelRegistration.presentation.mappers.toImovel
import com.fstengineering.leasilycrm.imovelRegistration.presentation.mappers.toUiState
import com.fstengineering.leasilycrm.imovelRegistration.presentation.models.ImovelDraftFormUiState
import com.fstengineering.leasilycrm.imovelRegistration.presentation.models.ProprietarioUiState
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

class ImovelRegistrationViewModel(
    private val readImovel: ReadImovelUseCase,
    private val imovelWriteDataSource: ImovelWriteDataSource,
    private val imovelQueryRepository: ImovelQueryRepository,
    private val proprietarioReadDataSource: ProprietarioReadDataSource,
) : ViewModel() {
    private var isInitialized = false

    var imovelId: Long? = null
        private set

    private val _uiEvents = Channel<UiEvent>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _proprietarios =
        MutableStateFlow<ImmutableList<ProprietarioUiState>>(persistentListOf())
    val proprietarios = _proprietarios.asStateFlow()

    private val _pastNeighborhoods =
        MutableStateFlow<ImmutableList<String>>(persistentListOf())
    val pastNeighborhoods = _pastNeighborhoods.asStateFlow()

    private val _draftFormUiState =
        MutableStateFlow<ImovelDraftFormUiState>(ImovelDraftFormUiState.Loading)
    val draftFormUiState = _draftFormUiState.asStateFlow()

    private var formToEdit: ImovelDraftFormUiState? = null

    fun init(imovelId: Long?) {
        if (isInitialized) return

        this.imovelId = imovelId

        if (imovelId != null) getImovelToEdit(imovelId)
        else _draftFormUiState.update { ImovelDraftFormUiState.Loaded.Empty }

        watchProprietarios()
        getPastNeighborhoods()

        isInitialized = true
    }

    private fun getImovelToEdit(imovelId: Long) {
        val query = ImovelArgs(id = imovelId)

        viewModelScope.launch {
            readImovel(query)
                .onError { e -> _uiEvents.trySend(UiEvent.BottomSheetError(e)) }
                .onSuccess { storedImovel ->
                    val draftForm = storedImovel.toUiState()

                    _draftFormUiState.update { draftForm }
                    formToEdit = draftForm
                }
        }
    }

    private fun watchProprietarios() {
        viewModelScope.launch {
            proprietarioReadDataSource.watchProprietarios().collectLatest { result ->
                result
                    .onError { e -> _uiEvents.trySend(UiEvent.BottomSheetError(e)) }
                    .onSuccess { storedProprietarios ->
                        val states = storedProprietarios.map { it.toUiState() }.toPersistentList()
                        _proprietarios.update { states }
                    }
            }
        }
    }

    private fun getPastNeighborhoods() {
        viewModelScope.launch {
            imovelQueryRepository.getAllNeighborhoodNames()
                .onError { e -> _uiEvents.trySend(UiEvent.BottomSheetError(e)) }
                .onSuccess { neighborhoodNames -> _pastNeighborhoods.update { neighborhoodNames } }
        }
    }

    fun onImovelFormChange(form: ImovelDraftFormUiState) {
        _draftFormUiState.update { form }
    }

    fun onImovelFilePick(contentUriString: String) {
        val form = (draftFormUiState.value as? ImovelDraftFormUiState.Loaded) ?: return

        val fileDraft = ImovelFileUiState(
            uri = contentUriString.toUri(),
            isMainPicture = false,
        )

        onImovelFormChange(form.addFile(fileDraft))
    }

    fun saveImovel() {
        val form = (draftFormUiState.value as? ImovelDraftFormUiState.Loaded) ?: return

        viewModelScope.launch {
            if (formToEdit != null && form == formToEdit) {
                _uiEvents.trySend(UiEvent.ImovelEditSuccess(messageId = R.string.no_data_changed_success_message))
                return@launch
            }

            val imovel = form.toImovel()
                .fold(
                    onError = {
                        // TODO: Show something in the UI with the Domain errors
                        _uiEvents.trySend(UiEvent.BottomSheetError(ApplicationError.Unknown))

                        return@launch
                    },
                    onSuccess = { it },
                )

            imovelWriteDataSource.save(imovel)
                .onError { error ->
                    _uiEvents.trySend(UiEvent.BottomSheetError(error))

                    return@launch
                }

            val saveEvent =
                if (formToEdit != null) UiEvent.ImovelEditSuccess(messageId = R.string.edit_property_success_message)
                else UiEvent.ImovelCreateSuccess

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

        data object ImovelCreateSuccess : UiEvent

        data class ImovelEditSuccess(val messageId: Int) : UiEvent

        data class Exit(val needConfirmation: Boolean) : UiEvent
    }
}
