package com.fstengineering.leasilycrm.occupiedPeriods.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.application.models.commands.RegisterCheckOutCommand
import com.fstengineering.leasilycrm.core.application.models.commands.UndoCheckOutCommand
import com.fstengineering.leasilycrm.core.application.repositories.OccupiedPeriodSharedCommandRepository
import com.fstengineering.leasilycrm.core.application.repositories.OccupiedPeriodSharedQueryRepository
import com.fstengineering.leasilycrm.core.application.useCases.RegisterStayCheckOutUseCase
import com.fstengineering.leasilycrm.core.application.useCases.UndoRegisterStayCheckOutUseCase
import com.fstengineering.leasilycrm.core.domain.utils.BaseError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.presentation.models.OccupiedPeriodFilterType
import com.fstengineering.leasilycrm.occupiedPeriods.application.models.dtos.OccupiedPeriodListReadModel
import com.fstengineering.leasilycrm.occupiedPeriods.application.repositories.OccupiedPeriodQueryRepository
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class OccupiedPeriodsViewModel(
    private val registerStayCheckOutUseCase: RegisterStayCheckOutUseCase,
    private val undoRegisterStayCheckOutUseCase: UndoRegisterStayCheckOutUseCase,
    private val occupiedPeriodSharedCommandRepository: OccupiedPeriodSharedCommandRepository,
    private val occupiedPeriodQueryRepository: OccupiedPeriodQueryRepository,
    private val occupiedPeriodSharedQueryRepository: OccupiedPeriodSharedQueryRepository,
) : ViewModel() {
    private var isInitialized = false

    private val _uiEvents = Channel<UiEvent>(Channel.BUFFERED)
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _occupiedPeriods =
        MutableStateFlow<ImmutableList<OccupiedPeriodListReadModel>>(persistentListOf())
    val occupiedPeriods = _occupiedPeriods.asStateFlow()

    private val _occupiedPeriodToDelete = MutableStateFlow<OccupiedPeriodListReadModel?>(null)
    val occupiedPeriodToDelete = _occupiedPeriodToDelete.asStateFlow()

    private var stayIdToRegisterCheckIn: Long? = null
    private var stayToRegisterCheckOut: OccupiedPeriodListReadModel? = null

    var mustNotShowCheckOutDialog: Boolean = true
        private set

    private val _currentOccupiedPeriodFilterType =
        MutableStateFlow(OccupiedPeriodFilterType.UpcomingCheckIn)
    val currentOccupiedPeriodFilterType = _currentOccupiedPeriodFilterType.asStateFlow()

    private val _staysAbleToCheckInCount = MutableStateFlow(0)
    val staysAbleToCheckInCount = _staysAbleToCheckInCount.asStateFlow()

    private val _staysAbleToCheckOutCount = MutableStateFlow(0)
    val staysAbleToCheckOutCount = _staysAbleToCheckOutCount.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun init() {
        if (isInitialized) return

        watchMustNotShowCheckOutDialogAnymore()

        watchStays()
        watchTodayCheckInsAndCheckouts()

        isInitialized = true
    }

    fun onPause() {
        isInitialized = false
    }

    private fun watchMustNotShowCheckOutDialogAnymore() {
        viewModelScope.launch {
            occupiedPeriodQueryRepository.watchMustNotShowCheckOutDialogAnymore()
                .collectLatest { result ->
                    result
                        .onError { e -> _uiEvents.trySend(UiEvent.ShowError(e)) }
                        .onSuccess { mustNotShowCheckOutDialogAnymore ->
                            mustNotShowCheckOutDialog = mustNotShowCheckOutDialogAnymore
                        }
                }
        }
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private fun watchStays() {
        viewModelScope.launch {
            currentOccupiedPeriodFilterType
                .flatMapLatest { newFilter ->
                    _isLoading.update { true }

                    occupiedPeriodQueryRepository.watchAll()
                        .map { it to newFilter }
                }
                .collectLatest { (watchAllResult, newFilter) ->
                    watchAllResult
                        .onError { e -> _uiEvents.trySend(UiEvent.ShowError(e)) }
                        .onSuccess { stays ->
                            _occupiedPeriods.update { stays.filterAndSortStays(filterType = newFilter) }
                        }

                    _isLoading.update { false }
                }
        }
    }

    private fun ImmutableList<OccupiedPeriodListReadModel>.filterAndSortStays(
        filterType: OccupiedPeriodFilterType,
    ): ImmutableList<OccupiedPeriodListReadModel> {
        val filteredStays = filter { stay ->
            when (filterType) {
                OccupiedPeriodFilterType.UpcomingCheckIn -> !stay.isCheckInDone && !stay.isCheckOutDone && !stay.isUnavailablePeriod
                OccupiedPeriodFilterType.UpcomingCheckOut -> stay.isCheckInDone && !stay.isCheckOutDone && !stay.isUnavailablePeriod
                OccupiedPeriodFilterType.Finished -> stay.isCheckInDone && stay.isCheckOutDone && !stay.isUnavailablePeriod
                OccupiedPeriodFilterType.UnavailablePeriod -> stay.isUnavailablePeriod
                OccupiedPeriodFilterType.All -> true
            }
        }

        val sortedStays = when (filterType) {
            OccupiedPeriodFilterType.Finished, OccupiedPeriodFilterType.All -> filteredStays.sortedByDescending { it.checkInDateTime }
            OccupiedPeriodFilterType.UpcomingCheckOut -> filteredStays.sortedBy { it.checkOutDateTime }
            else -> filteredStays.sortedBy { it.checkInDateTime }
        }

        return sortedStays.toPersistentList()
    }

    private fun watchTodayCheckInsAndCheckouts() {
        viewModelScope.launch {
            occupiedPeriodSharedQueryRepository.watchAllPastCheckInCheckOutNotDoneCounts()
                .collectLatest { result ->
                    result
                        .onError { e -> _uiEvents.trySend(UiEvent.ShowError(e)) }
                        .onSuccess { (staysAbleToCheckInCount, staysAbleToCheckOutCount) ->
                            _staysAbleToCheckInCount.update { staysAbleToCheckInCount }
                            _staysAbleToCheckOutCount.update { staysAbleToCheckOutCount }
                        }
                }
        }
    }

    fun onStayFilterTypeChange(occupiedPeriodFilterType: OccupiedPeriodFilterType) {
        _currentOccupiedPeriodFilterType.update { occupiedPeriodFilterType }
    }

    fun onDeleteStay(stay: OccupiedPeriodListReadModel?) {
        _occupiedPeriodToDelete.update { stay }
    }

    fun deleteStay() {
        viewModelScope.launch {
            val stayToDelete =
                this@OccupiedPeriodsViewModel.occupiedPeriodToDelete.value ?: return@launch

            occupiedPeriodSharedCommandRepository.delete(id = stayToDelete.id)
                .onError { e -> _uiEvents.trySend(UiEvent.ShowError(e)) }

            _uiEvents.trySend(UiEvent.FinishOccupiedPeriodDelete)
        }
    }

    fun registerStayCheckIn(stay: OccupiedPeriodListReadModel) {
        viewModelScope.launch {
            stayIdToRegisterCheckIn = stay.id

            occupiedPeriodSharedCommandRepository.registerCheckIn(id = stay.id)
                .onError { e -> _uiEvents.trySend(UiEvent.ShowError(e)) }
                .onSuccess {
                    _uiEvents.trySend(UiEvent.RegisterCheckInSuccess)

                    delay(100.milliseconds)
                    _currentOccupiedPeriodFilterType.update { OccupiedPeriodFilterType.UpcomingCheckOut }
                }
        }
    }

    fun undoRegisterStayCheckIn() {
        val id = stayIdToRegisterCheckIn ?: return

        viewModelScope.launch {
            occupiedPeriodSharedCommandRepository.undoRegisterCheckIn(id = id)
                .onError { e -> _uiEvents.trySend(UiEvent.ShowError(e)) }
        }
    }

    fun onRegisterStayCheckOut(stay: OccupiedPeriodListReadModel) {
        stayToRegisterCheckOut = stay
    }

    fun registerStayCheckOut(
        mustNotShowCheckOutDialogAnymore: Boolean = true,
    ) {
        val stay = stayToRegisterCheckOut ?: return

        viewModelScope.launch {
            registerStayCheckOutUseCase(
                RegisterCheckOutCommand(
                    stayId = stay.id,
                    tenantId = stay.tenantId
                )
            )
                .onError { error ->
                    _uiEvents.trySend(UiEvent.ShowError(error))
                }
                .onSuccess {
                    _uiEvents.trySend(UiEvent.RegisterCheckOutSuccess)

                    delay(100.milliseconds)
                    _currentOccupiedPeriodFilterType.update { OccupiedPeriodFilterType.Finished }
                }
        }

        viewModelScope.launch {
            occupiedPeriodSharedCommandRepository.setMustNotShowCheckOutDialogAnymore(
                mustNotShowCheckOutDialogAnymore = mustNotShowCheckOutDialogAnymore,
            )
                .onError { e -> _uiEvents.trySend(UiEvent.ShowError(e)) }
        }
    }

    fun undoRegisterStayCheckOut() {
        val stay = stayToRegisterCheckOut ?: return

        viewModelScope.launch {
            undoRegisterStayCheckOutUseCase(
                UndoCheckOutCommand(
                    stayId = stay.id,
                    tenantId = stay.tenantId
                )
            )
                .onError { error ->
                    _uiEvents.trySend(UiEvent.ShowError(error))
                }
        }
    }

    sealed interface UiEvent {
        data class ShowError(val error: BaseError) : UiEvent
        data object FinishOccupiedPeriodDelete : UiEvent
        data object RegisterCheckInSuccess : UiEvent
        data object RegisterCheckOutSuccess : UiEvent
    }
}
