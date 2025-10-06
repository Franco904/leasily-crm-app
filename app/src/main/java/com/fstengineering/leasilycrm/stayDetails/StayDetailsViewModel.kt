package com.fstengineering.leasilycrm.stayDetails

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.domain.models.Stay
import com.fstengineering.leasilycrm.core.domain.repositories.StayRepository
import com.fstengineering.leasilycrm.core.domain.repositories.UserRepository
import com.fstengineering.leasilycrm.core.domain.useCases.interfaces.DeleteStayUseCase
import com.fstengineering.leasilycrm.core.domain.useCases.interfaces.GetStaysStructuredDatesUseCase
import com.fstengineering.leasilycrm.core.domain.useCases.interfaces.RegisterStayCheckOutUseCase
import com.fstengineering.leasilycrm.core.domain.useCases.interfaces.StaysStructuredDatesMap
import com.fstengineering.leasilycrm.core.domain.useCases.interfaces.UndoRegisterStayCheckOutUseCase
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toUiMessage
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StayDetailsViewModel(
    private val deleteStayUseCase: DeleteStayUseCase,
    private val registerStayCheckOutUseCase: RegisterStayCheckOutUseCase,
    private val undoRegisterStayCheckOutUseCase: UndoRegisterStayCheckOutUseCase,
    private val getStaysStructuredDatesUseCase: GetStaysStructuredDatesUseCase,
    private val stayRepository: StayRepository,
    private val userRepository: UserRepository,
) : ViewModel() {
    private var isInitialized = false

    private val _uiEvents = Channel<UiEvents>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _stay = MutableStateFlow(Stay())
    val stay = _stay.asStateFlow()

    private val _stayStructuredDates = MutableStateFlow<StaysStructuredDatesMap>(persistentMapOf())
    val stayStructuredDates = _stayStructuredDates.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    var mustNotShowCheckOutDialog: Boolean = true
        private set

    fun init(stayId: Long) {
        if (isInitialized) return

        watchSessionExpiration()
        watchStay(stayId = stayId)

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

    private fun watchStay(stayId: Long) {
        _isLoading.update { true }

        viewModelScope.launch {
            stayRepository.watchById(stayId).collect { result ->
                result
                    .onError { error ->
                        _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                    }
                    .onSuccess { storedStay ->
                        if (storedStay == null) {
                            _uiEvents.send(UiEvents.NavigateUp)
                            return@onSuccess
                        }

                        _stay.update { storedStay }

                        _stayStructuredDates.update {
                            getStaysStructuredDatesUseCase(stays = persistentListOf(storedStay))
                        }
                    }

                _isLoading.update { false }
            }
        }
    }

    fun deleteStay() {
        viewModelScope.launch {
            deleteStayUseCase(stay.value)
                .onError { error ->
                    if (error.isRedirectError()) {
                        userRepository.deleteCachedUser()
                        _uiEvents.send(UiEvents.RedirectToSignIn)
                    }

                    _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                    return@launch
                }

            _uiEvents.send(UiEvents.StayDeleteSuccess)
        }
    }

    fun registerStayCheckIn() {
        viewModelScope.launch {
            val updatedStay = stay.value.copy(isCheckInDone = true)

            stayRepository.insertOrUpdate(stay = updatedStay)
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
                }
        }
    }

    fun undoRegisterStayCheckIn() {
        viewModelScope.launch {
            val updatedStay = stay.value.copy(isCheckInDone = false)

            stayRepository.insertOrUpdate(stay = updatedStay)
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

    fun registerStayCheckOut(
        mustNotShowCheckOutDialogAnymore: Boolean = true,
    ) {
        viewModelScope.launch {
            registerStayCheckOutUseCase(stay = stay.value)
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
            undoRegisterStayCheckOutUseCase(stay = stay.value)
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

        data object NavigateUp : UiEvents

        data object StayDeleteSuccess : UiEvents

        data object RegisterCheckInSuccess : UiEvents

        data object RegisterCheckOutSuccess : UiEvents
    }
}
