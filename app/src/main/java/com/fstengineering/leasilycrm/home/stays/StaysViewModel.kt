package com.fstengineering.leasilycrm.home.stays

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.application.repositories.StaySharedCommandRepository
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.RegisterStayCheckOutUseCase
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.UndoRegisterStayCheckOutUseCase
import com.fstengineering.leasilycrm.core.domain.entities.Stay
import com.fstengineering.leasilycrm.core.domain.helpers.AppDateTime
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.infra.utils.InfraError
import com.fstengineering.leasilycrm.core.presentation.models.StayFilterType
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class StaysViewModel(
    private val registerStayCheckOutUseCase: RegisterStayCheckOutUseCase,
    private val undoRegisterStayCheckOutUseCase: UndoRegisterStayCheckOutUseCase,
    private val staySharedCommandRepository: StaySharedCommandRepository,
    private val now: AppDateTime,
) : ViewModel() {
    private var isInitialized = false

    private val _uiEvents = Channel<UiEvents>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _stays = MutableStateFlow<ImmutableList<Stay>>(persistentListOf())
    val stays = _stays.asStateFlow()

    private val _stayToDelete = MutableStateFlow<Stay?>(null)
    val stayToDelete = _stayToDelete.asStateFlow()

    private var stayToRegisterCheckIn: Stay? = null
    private var stayToRegisterCheckOut: Stay? = null

    var mustNotShowCheckOutDialog: Boolean = true
        private set

    private val _currentStayFilterType = MutableStateFlow(StayFilterType.UpcomingCheckIn)
    val currentStayFilterType = _currentStayFilterType.asStateFlow()

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
            staySharedCommandRepository.watchMustNotShowCheckOutDialogAnymore().collect { result ->
                result
                    .onError { e -> _uiEvents.send(UiEvents.Error(e)) }
                    .onSuccess { mustNotShowCheckOutDialogAnymore ->
                        mustNotShowCheckOutDialog = mustNotShowCheckOutDialogAnymore
                    }
            }
        }
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private fun watchStays() {
        viewModelScope.launch {
            currentStayFilterType
                .flatMapLatest { newFilter ->
                    _isLoading.update { true }

                    staySharedCommandRepository.watchAll()
                        .map { it to newFilter }
                }
                .collect { (watchAllResult, newFilter) ->
                    watchAllResult
                        .onError { e -> _uiEvents.send(UiEvents.Error(e)) }
                        .onSuccess { stays ->
                            _stays.update { stays.filterAndSortStays(filterType = newFilter) }
                        }

                    _isLoading.update { false }
                }
        }
    }

    private fun ImmutableList<Stay>.filterAndSortStays(
        filterType: StayFilterType,
    ): ImmutableList<Stay> {
        val filteredStays = filter { stay ->
            when (filterType) {
                StayFilterType.UpcomingCheckIn -> !stay.isCheckInDone && !stay.isCheckOutDone && !stay.isUnavailablePeriod
                StayFilterType.UpcomingCheckOut -> stay.isCheckInDone && !stay.isCheckOutDone && !stay.isUnavailablePeriod
                StayFilterType.Finished -> stay.isCheckInDone && stay.isCheckOutDone && !stay.isUnavailablePeriod
                StayFilterType.UnavailablePeriod -> stay.isUnavailablePeriod
                StayFilterType.All -> true
            }
        }

        val sortedStays = when (filterType) {
            StayFilterType.Finished, StayFilterType.All -> filteredStays.sortedByDescending { it.checkInDateTime?.dateTime() }
            StayFilterType.UpcomingCheckOut -> filteredStays.sortedBy { it.checkOutDateTime?.dateTime() }
            else -> filteredStays.sortedBy { it.checkInDateTime?.dateTime() }
        }

        return sortedStays.toPersistentList()
    }

    private fun watchTodayCheckInsAndCheckouts() {
        viewModelScope.launch {
            staySharedCommandRepository.watchAllUpcomingCheckInsAndCheckOuts().collect { result ->
                result
                    .onError { e -> _uiEvents.send(UiEvents.Error(e)) }
                    .onSuccess { upcomingCheckInsAndCheckOuts ->
                        val staysAbleToCheckInCount = upcomingCheckInsAndCheckOuts.count {
                            !it.isCheckInDone && now.isAfter(it.checkInDateTime.toStartOfDay())
                        }

                        val staysAbleToCheckOutCount = upcomingCheckInsAndCheckOuts.count {
                            it.isCheckInDone && now.isAfter(it.checkOutDateTime.toStartOfDay())
                        }

                        _staysAbleToCheckInCount.update { staysAbleToCheckInCount }
                        _staysAbleToCheckOutCount.update { staysAbleToCheckOutCount }
                    }
            }
        }
    }

    fun onStayFilterTypeChange(stayFilterType: StayFilterType) {
        _currentStayFilterType.update { stayFilterType }
    }

    fun onDeleteStay(stay: Stay?) {
        _stayToDelete.update { stay }
    }

    fun deleteStay() {
        viewModelScope.launch {
            val stayToDelete = this@StaysViewModel.stayToDelete.value ?: return@launch

            staySharedCommandRepository.delete(id = stayToDelete.id!!)
                .onError { e -> _uiEvents.send(UiEvents.Error(e)) }

            _uiEvents.send(UiEvents.StayDeleteSuccess)
        }
    }

    fun registerStayCheckIn(stay: Stay) {
        viewModelScope.launch {
            stayToRegisterCheckIn = stay.copy(isCheckInDone = true)

            staySharedCommandRepository.insertOrUpdate(stay = stayToRegisterCheckIn!!)
                .onError { e -> _uiEvents.send(UiEvents.Error(e)) }
                .onSuccess {
                    _uiEvents.send(UiEvents.RegisterCheckInSuccess)

                    delay(100.milliseconds)
                    _currentStayFilterType.update { StayFilterType.UpcomingCheckOut }
                }
        }
    }

    fun undoRegisterStayCheckIn() {
        viewModelScope.launch {
            stayToRegisterCheckIn = stayToRegisterCheckIn?.copy(isCheckInDone = false)

            staySharedCommandRepository.insertOrUpdate(stay = stayToRegisterCheckIn!!)
                .onError { e -> _uiEvents.send(UiEvents.Error(e)) }
        }
    }

    fun onRegisterStayCheckOut(stay: Stay) {
        stayToRegisterCheckOut = stay
    }

    fun registerStayCheckOut(
        mustNotShowCheckOutDialogAnymore: Boolean = true,
    ) {
        if (stayToRegisterCheckOut == null) return

        viewModelScope.launch {
            registerStayCheckOutUseCase(stay = stayToRegisterCheckOut!!)
                .onError { e -> _uiEvents.send(UiEvents.Error(e)) }
                .onSuccess {
                    _uiEvents.send(UiEvents.RegisterCheckOutSuccess)

                    delay(100.milliseconds)
                    _currentStayFilterType.update { StayFilterType.Finished }
                }
        }

        viewModelScope.launch {
            staySharedCommandRepository.setMustNotShowCheckOutDialogAnymore(
                mustNotShowCheckOutDialogAnymore = mustNotShowCheckOutDialogAnymore,
            )
                .onError { e -> _uiEvents.send(UiEvents.Error(e)) }
        }
    }

    fun undoRegisterStayCheckOut() {
        viewModelScope.launch {
            undoRegisterStayCheckOutUseCase(stay = stayToRegisterCheckOut!!)
                .onError { e -> _uiEvents.send(UiEvents.Error(e)) }
        }
    }

    sealed interface UiEvents {
        data class Error(val error: InfraError) : UiEvents
        data object StayDeleteSuccess : UiEvents
        data object RegisterCheckInSuccess : UiEvents
        data object RegisterCheckOutSuccess : UiEvents
    }
}
