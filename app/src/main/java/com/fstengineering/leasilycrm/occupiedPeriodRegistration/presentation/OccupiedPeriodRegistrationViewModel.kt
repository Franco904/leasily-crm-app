package com.fstengineering.leasilycrm.occupiedPeriodRegistration.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.R
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.BaseError
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.ValidationErrors
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.infra.sharedStorage.SharedStorage
import com.fstengineering.leasilycrm.core.presentation.models.ClienteUiState
import com.fstengineering.leasilycrm.core.presentation.models.ImovelCardUiState
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toInlineMessage
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.commands.StayCommandModel
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.commands.UnavailablePeriodCommandModel
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.queries.SelectableImoveisArgs
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.queries.StayArgs
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.queries.UnavailablePeriodArgs
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.repositories.ImovelQueryRepository
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.repositories.TenantQueryRepository
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.useCases.ReadSelectableImoveisUseCase
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.useCases.ReadStayUseCase
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.useCases.ReadUnavailablePeriodUseCase
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.useCases.WriteStayUseCase
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.useCases.WriteUnavailablePeriodUseCase
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.presentation.mappers.toCommandModel
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.presentation.mappers.toUiState
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.presentation.models.OccupiedPeriodErrorUiState
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.presentation.models.OccupiedPeriodField
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.presentation.models.OccupiedPeriodFormUiState
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.presentation.models.StayFileUiState
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OccupiedPeriodRegistrationViewModel(
    private val readStay: ReadStayUseCase,
    private val readUnavailablePeriod: ReadUnavailablePeriodUseCase,
    private val readSelectableImoveis: ReadSelectableImoveisUseCase,
    private val writeStay: WriteStayUseCase,
    private val writeUnavailablePeriod: WriteUnavailablePeriodUseCase,
    private val imovelQueryRepository: ImovelQueryRepository,
    private val tenantQueryRepository: TenantQueryRepository,
    private val sharedStorage: SharedStorage,
) : ViewModel() {
    private var isInitialized = false

    private val _uiEvents = Channel<UiEvent>(Channel.BUFFERED)
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _imoveis =
        MutableStateFlow<ImmutableList<ImovelCardUiState>>(persistentListOf())
    val imoveis = _imoveis.asStateFlow()

    private val _tenants = MutableStateFlow<ImmutableList<ClienteUiState>>(persistentListOf())
    val tenants = _tenants.asStateFlow()

    private val _draftFormUiState =
        MutableStateFlow<OccupiedPeriodFormUiState>(OccupiedPeriodFormUiState.Loading)
    val draftFormUiState = _draftFormUiState.asStateFlow()

    private var formToEdit: OccupiedPeriodFormUiState? = null

    private val _formErrors = MutableStateFlow(OccupiedPeriodErrorUiState())
    val formErrors = _formErrors.asStateFlow()

    private val _imovelSearchInput = MutableStateFlow<String?>(null)
    val imovelSearchInput = _imovelSearchInput.asStateFlow()

    private val _tenantSearchInput = MutableStateFlow<String?>(null)
    val tenantSearchInput = _tenantSearchInput.asStateFlow()

    fun init(
        occupiedPeriodId: Long?,
        imovelId: Long?,
        tenantId: Long?,
        isUnavailablePeriod: Boolean,
    ) {
        if (isInitialized) return

        loadData(occupiedPeriodId, imovelId, tenantId, isUnavailablePeriod)

        isInitialized = true
    }

    private fun loadData(
        occupiedPeriodId: Long?,
        imovelId: Long?,
        tenantId: Long?,
        isUnavailablePeriod: Boolean,
    ) {
        viewModelScope.launch {
            if (isUnavailablePeriod) {
                readUnavailablePeriod(UnavailablePeriodArgs(id = occupiedPeriodId))
                    .onError { e -> _uiEvents.trySend(UiEvent.ShowError(e)) }
                    .onSuccess { storedUnavailablePeriod ->
                        val draftForm = storedUnavailablePeriod.toUiState()

                        _draftFormUiState.update { draftForm }
                        formToEdit = occupiedPeriodId?.let { draftForm }
                    }
            } else {
                readStay(StayArgs(id = occupiedPeriodId))
                    .onError { e -> _uiEvents.trySend(UiEvent.ShowError(e)) }
                    .onSuccess { storedStay ->
                        val draftForm = storedStay.toUiState()

                        _draftFormUiState.update { draftForm }
                        formToEdit = occupiedPeriodId?.let { draftForm }
                    }
            }

            if (occupiedPeriodId == null) {
                if (imovelId != null) loadImovelToAutofill(imovelId)
                if (tenantId != null) loadTenantToAutofill(tenantId)
            }

            watchImovelSearchInput(occupiedPeriodId)
            watchTenantSearchInput()
        }
    }

    private fun loadImovelToAutofill(imovelId: Long) {
        viewModelScope.launch {
            imovelQueryRepository.getById(imovelId)
                .onError { e -> _uiEvents.trySend(UiEvent.ShowError(e)) }
                .onSuccess { storedImovel ->
                    _draftFormUiState.update {
                        when (it) {
                            is OccupiedPeriodFormUiState.Loaded.Stay -> it.copy(imovel = storedImovel.toUiState())
                            is OccupiedPeriodFormUiState.Loaded.UnavailablePeriod -> it.copy(
                                imovel = storedImovel.toUiState()
                            )

                            else -> it
                        }
                    }
                }
        }
    }

    private fun loadTenantToAutofill(tenantId: Long) {
        viewModelScope.launch {
            tenantQueryRepository.getById(tenantId)
                .onError { e -> _uiEvents.trySend(UiEvent.ShowError(e)) }
                .onSuccess { storedTenant ->
                    _draftFormUiState.update {
                        when (it) {
                            is OccupiedPeriodFormUiState.Loaded.Stay -> it.copy(tenant = storedTenant.toUiState())
                            else -> it
                        }
                    }
                }
        }
    }

    @OptIn(FlowPreview::class)
    fun watchImovelSearchInput(stayId: Long?) {
        viewModelScope.launch {
            imovelSearchInput
                .debounce(500.milliseconds)
                .collectLatest { searchInput ->
                    val args = SelectableImoveisArgs(
                        matching = searchInput,
                        stayId = stayId,
                    )

                    readSelectableImoveis(args)
                        .onError { e -> _uiEvents.trySend(UiEvent.ShowError(e)) }
                        .onSuccess { matchingImoveis ->
                            val imoveis =
                                matchingImoveis.map { it.toUiState() }.toPersistentList()
                            _imoveis.update { imoveis }
                        }
                }
        }
    }

    @OptIn(FlowPreview::class)
    fun watchTenantSearchInput() {
        viewModelScope.launch {
            tenantSearchInput
                .debounce(500.milliseconds)
                .collectLatest { searchInput ->
                    val tenant = (formToEdit as? OccupiedPeriodFormUiState.Loaded.Stay)?.tenant

                    tenantQueryRepository.getAllWithoutUpcomingStays(
                        matching = searchInput,
                        currentTenantId = tenant?.id,
                    )
                        .onError { e -> _uiEvents.trySend(UiEvent.ShowError(e)) }
                        .onSuccess { matchingTenants ->
                            val tenants =
                                matchingTenants.map { it.toUiState() }.toPersistentList()
                            _tenants.update { tenants }
                        }
                }
        }
    }

    fun onImovelSearchInputChange(searchInput: String?) {
        _imovelSearchInput.update { searchInput?.ifBlank { null } }
    }

    fun onTenantSearchInputChange(searchInput: String?) {
        _tenantSearchInput.update { searchInput?.ifBlank { null } }
    }

    fun onOwnedStayFormChange(newForm: OccupiedPeriodFormUiState.Loaded.Stay) {
        val form = (draftFormUiState.value as? OccupiedPeriodFormUiState.Loaded.Stay) ?: return

        _formErrors.update {
            val newErrors = it.errors.mutate { map ->
                if (form.imovel != newForm.imovel) map[OccupiedPeriodField.Imovel] = null
                if (form.tenant != newForm.tenant) map[OccupiedPeriodField.Tenant] = null
                if (form.checkInDateTime != newForm.checkInDateTime) map[OccupiedPeriodField.Period] =
                    null
                if (form.checkInTime != newForm.checkInTime) map[OccupiedPeriodField.CheckInTime] =
                    null
                if (form.checkOutTime != newForm.checkOutTime) map[OccupiedPeriodField.CheckOutTime] =
                    null
                if (form.commission != newForm.commission) map[OccupiedPeriodField.Commission] =
                    null
            }

            it.copy(errors = newErrors)
        }

        _draftFormUiState.update { form }
    }

    fun onUnavailablePeriodFormChange(newForm: OccupiedPeriodFormUiState.Loaded.UnavailablePeriod) {
        val form =
            (draftFormUiState.value as? OccupiedPeriodFormUiState.Loaded.UnavailablePeriod)
                ?: return

        _formErrors.update {
            val newErrors = it.errors.mutate { map ->
                if (form.imovel != newForm.imovel) map[OccupiedPeriodField.Imovel] = null
                if (form.checkInDateTime != newForm.checkInDateTime) map[OccupiedPeriodField.Period] =
                    null
            }

            it.copy(errors = newErrors)
        }

        _draftFormUiState.update { form }
    }

    fun clearCommissionError() {
        _formErrors.update {
            val newErrors = it.errors.mutate { map -> map[OccupiedPeriodField.Commission] = null }
            it.copy(errors = newErrors)
        }
    }

    fun onStayFilePick(
        contentUriString: String,
        defaultFileName: String,
    ) {
        val form = (draftFormUiState.value as? OccupiedPeriodFormUiState.Loaded.Stay) ?: return

        viewModelScope.launch {
            val fileName = sharedStorage.getFileName(contentUriString)

            val fileDraft = StayFileUiState(
                uriString = contentUriString,
                name = fileName ?: defaultFileName,
            )

            onOwnedStayFormChange(newForm = form.addFile(fileDraft))
        }
    }

    fun saveStay() {
        if (formToEdit != null && draftFormUiState.value == formToEdit) {
            _uiEvents.trySend(UiEvent.FinishStayEdit(messageId = R.string.no_data_changed_success_message))
            return
        }

        val command = when (val form = draftFormUiState.value) {
            is OccupiedPeriodFormUiState.Loaded.Stay -> form.toCommandModel()
            is OccupiedPeriodFormUiState.Loaded.UnavailablePeriod -> form.toCommandModel()
            else -> return
        }

        viewModelScope.launch {
            val editMessageId = when (command) {
                is StayCommandModel -> {
                    writeStay(command)
                        .onError { error ->
                            when (error) {
                                is ValidationErrors -> setFieldErrors(error.errors)
                                is DomainError -> setFieldErrors(listOf(error))
                                is ApplicationError ->
                                    _uiEvents.trySend(UiEvent.ShowError(error))
                            }

                            return@launch
                        }

                    R.string.edit_stay_success_message
                }

                is UnavailablePeriodCommandModel -> {
                    writeUnavailablePeriod(command)
                        .onError { error ->
                            when (error) {
                                is ValidationErrors -> setFieldErrors(error.errors)
                                is DomainError -> setFieldErrors(listOf(error))
                                is ApplicationError ->
                                    _uiEvents.trySend(UiEvent.ShowError(error))
                            }

                            return@launch
                        }

                    R.string.edit_unavailable_period_success_message
                }

                else -> return@launch
            }

            val saveEvent =
                if (formToEdit != null) UiEvent.FinishStayEdit(messageId = editMessageId)
                else UiEvent.FinishStayCreate

            _uiEvents.trySend(saveEvent)
        }
    }

    private suspend fun setFieldErrors(errors: List<DomainError>) {
        _formErrors.update { OccupiedPeriodErrorUiState() }
        delay(150.milliseconds)

        val stayImovelError = errors.firstOrNull { it is DomainError.StayImovel }
        val stayTenantError = errors.firstOrNull { it is DomainError.StayTenant }
        val periodError = errors.firstOrNull { it is DomainError.StayPeriod }
        val checkInTimeError = errors.firstOrNull { it is DomainError.StayCheckInTime }
        val checkOutTimeError = errors.firstOrNull { it is DomainError.StayCheckOutTime }
        val commissionError = errors.firstOrNull { it is DomainError.StayCommission }

        _formErrors.update {
            val newErrors = it.errors.mutate { map ->
                map[OccupiedPeriodField.Imovel] = stayImovelError?.toInlineMessage()
                map[OccupiedPeriodField.Tenant] = stayTenantError?.toInlineMessage()
                map[OccupiedPeriodField.Period] = periodError?.toInlineMessage()
                map[OccupiedPeriodField.CheckInTime] = checkInTimeError?.toInlineMessage()
                map[OccupiedPeriodField.CheckOutTime] = checkOutTimeError?.toInlineMessage()
                map[OccupiedPeriodField.Commission] = commissionError?.toInlineMessage()
            }

            it.copy(errors = newErrors)
        }

        val firstFieldWithError =
            _formErrors.value.errors.entries.first { it.value != null }.key
        _uiEvents.trySend(UiEvent.ScrollToField(firstFieldWithError))
    }

    fun onExit() {
        val needConfirmation = when {
            formToEdit != null -> {
                formToEdit != draftFormUiState.value
            }

            else -> {
                val loadedForm = (draftFormUiState.value as? OccupiedPeriodFormUiState.Loaded)
                loadedForm?.isAnyFormFieldInitialized ?: false
            }
        }

        _uiEvents.trySend(UiEvent.Exit(needConfirmation))
    }

    override fun onCleared() {
        _uiEvents.close()

        super.onCleared()
    }

    sealed interface UiEvent {
        data class ShowError(val error: BaseError) : UiEvent

        data class ScrollToField(val field: OccupiedPeriodField) : UiEvent

        data class FinishStayEdit(val messageId: Int) : UiEvent

        data object FinishStayCreate : UiEvent

        data class Exit(val needConfirmation: Boolean) : UiEvent
    }
}
