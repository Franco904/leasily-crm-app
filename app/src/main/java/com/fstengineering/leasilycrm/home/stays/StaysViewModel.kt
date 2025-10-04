package com.fstengineering.leasilycrm.home.stays

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.domain.models.Stay
import com.fstengineering.leasilycrm.core.domain.repositories.StayRepository
import com.fstengineering.leasilycrm.core.domain.repositories.UserRepository
import com.fstengineering.leasilycrm.core.domain.useCases.interfaces.DeleteStayUseCase
import com.fstengineering.leasilycrm.core.domain.useCases.interfaces.RegisterStayCheckOutUseCase
import com.fstengineering.leasilycrm.core.domain.useCases.interfaces.UndoRegisterStayCheckOutUseCase
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toUiMessage
import com.fstengineering.leasilycrm.home.stays.models.StayFilterType
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
    private val deleteStayUseCase: DeleteStayUseCase,
    private val registerStayCheckOutUseCase: RegisterStayCheckOutUseCase,
    private val undoRegisterStayCheckOutUseCase: UndoRegisterStayCheckOutUseCase,
    private val stayRepository: StayRepository,
    private val userRepository: UserRepository,
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

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun init() {
        if (isInitialized) return

        watchSessionExpiration()
        watchMustNotShowCheckOutDialogAnymore()

        watchStays()

        isInitialized = true
    }

    private fun watchSessionExpiration() {
        viewModelScope.launch {
            userRepository.watchSessionExpiration().collect { result ->
                result
                    .onError { error ->
                        _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                    }
                    .onSuccess { hasSessionExpired ->
                        if (hasSessionExpired) {
                            userRepository.deleteCachedUser()
                            _uiEvents.send(UiEvents.RedirectToSignIn)
                        }
                    }
            }
        }
    }

    private fun watchMustNotShowCheckOutDialogAnymore() {
        viewModelScope.launch {
            stayRepository.watchMustNotShowCheckOutDialogAnymore().collect { result ->
                result
                    .onError { error ->
                        _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                    }
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

                    stayRepository.watchAll()
                        .map { it to newFilter }
                }
                .collect { (result, newFilter) ->
                    result
                        .onError { error ->
                            _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                        }
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
            if (stay.isDeleted) return@filter false

            when (filterType) {
                StayFilterType.UpcomingCheckIn -> !stay.isCheckInDone && !stay.isCheckOutDone && !stay.isFromOtherRealtor
                StayFilterType.UpcomingCheckOut -> stay.isCheckInDone && !stay.isCheckOutDone && !stay.isFromOtherRealtor
                StayFilterType.Finished -> stay.isCheckInDone && stay.isCheckOutDone && !stay.isFromOtherRealtor
                StayFilterType.UnavailablePeriod -> stay.isFromOtherRealtor
                StayFilterType.All -> true
            }
        }

        val sortedStays = when (filterType) {
            StayFilterType.Finished, StayFilterType.All -> filteredStays.sortedByDescending { it.checkInDateTime }
            else -> filteredStays.sortedBy { it.checkInDateTime }
        }

        return sortedStays.toPersistentList()
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

            deleteStayUseCase(stayToDelete)
                .onError { error ->
                    if (error.isRedirectError()) {
                        userRepository.deleteCachedUser()
                        _uiEvents.send(UiEvents.RedirectToSignIn)
                    }

                    _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))

                    return@launch
                }

            _uiEvents.send(UiEvents.StayDeleteSuccess)
            onDeleteStay(stay = null)
        }
    }

    fun registerStayCheckIn(stay: Stay) {
        viewModelScope.launch {
            stayToRegisterCheckIn = stay.copy(isCheckInDone = true)

            stayRepository.insertOrUpdate(stay = stayToRegisterCheckIn!!)
                .onError { error ->
                    if (error.isRedirectError()) {
                        userRepository.deleteCachedUser()
                        _uiEvents.send(UiEvents.RedirectToSignIn)
                    }

                    _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))

                    return@launch
                }
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

            stayRepository.insertOrUpdate(stay = stayToRegisterCheckIn!!)
                .onError { error ->
                    if (error.isRedirectError()) {
                        userRepository.deleteCachedUser()
                        _uiEvents.send(UiEvents.RedirectToSignIn)
                    }

                    _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))

                    return@launch
                }
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
                .onError { error ->
                    if (error.isRedirectError()) {
                        userRepository.deleteCachedUser()
                        _uiEvents.send(UiEvents.RedirectToSignIn)
                    }

                    _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                    return@launch
                }
                .onSuccess {
                    _uiEvents.send(UiEvents.RegisterCheckOutSuccess)

                    delay(100.milliseconds)
                    _currentStayFilterType.update { StayFilterType.Finished }
                }
        }

        viewModelScope.launch {
            stayRepository.setMustNotShowCheckOutDialogAnymore(
                mustNotShowCheckOutDialogAnymore = mustNotShowCheckOutDialogAnymore,
            )
                .onError { error ->
                    if (error.isRedirectError()) {
                        userRepository.deleteCachedUser()
                        _uiEvents.send(UiEvents.RedirectToSignIn)
                    }

                    _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                    return@launch
                }
        }
    }

    fun undoRegisterStayCheckOut() {
        viewModelScope.launch {
            undoRegisterStayCheckOutUseCase(stay = stayToRegisterCheckOut!!)
                .onError { error ->
                    if (error.isRedirectError()) {
                        userRepository.deleteCachedUser()
                        _uiEvents.send(UiEvents.RedirectToSignIn)
                    }

                    _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))

                    return@launch
                }
        }
    }

    sealed interface UiEvents {
        data class DataSourceError(@StringRes val messageId: Int) : UiEvents

        data object RedirectToSignIn : UiEvents

        data object StayDeleteSuccess : UiEvents

        data object RegisterCheckInSuccess : UiEvents

        data object RegisterCheckOutSuccess : UiEvents
    }
}
