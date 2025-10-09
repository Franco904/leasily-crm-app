package com.fstengineering.leasilycrm.home

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.application.models.AppLazyDateTime
import com.fstengineering.leasilycrm.core.domain.models.Assistance
import com.fstengineering.leasilycrm.core.domain.models.Tenant
import com.fstengineering.leasilycrm.core.domain.repositories.StayRepository
import com.fstengineering.leasilycrm.core.domain.repositories.UserRepository
import com.fstengineering.leasilycrm.core.domain.useCases.interfaces.DeleteCurrentAssistanceUseCase
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.presentation.utils.uiMappers.toUiMessage
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val deleteCurrentAssistanceUseCase: DeleteCurrentAssistanceUseCase,
    private val stayRepository: StayRepository,
    private val userRepository: UserRepository,
    private val now: AppLazyDateTime,
) : ViewModel() {
    private var isInitialized = false

    private val _uiEvents = Channel<UiEvents>()
    val uiEvents = _uiEvents.receiveAsFlow()

    private val _userProfilePictureUrl = MutableStateFlow<String?>(null)
    val userProfilePictureUrl = _userProfilePictureUrl.asStateFlow()

    private val _todayCheckInsAndCheckoutsCount = MutableStateFlow(0)
    val todayCheckInsAndCheckoutsCount = _todayCheckInsAndCheckoutsCount.asStateFlow()

    fun init() {
        if (isInitialized) return

        watchSessionExpiration()
        watchUserProfilePictureUrl()
        watchTodayCheckInsAndCheckouts()

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

    fun watchUserProfilePictureUrl() {
        viewModelScope.launch {
            userRepository.watchUser().collect { result ->
                result
                    .onError { error ->
                        if (error.isRedirectError()) {
                            userRepository.deleteCachedUser()
                            _uiEvents.send(UiEvents.RedirectToSignIn)
                        }

                        _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                    }
                    .onSuccess { user ->
                        _userProfilePictureUrl.value = user.profilePictureUrl
                    }
            }
        }
    }

    private fun watchTodayCheckInsAndCheckouts() {
        viewModelScope.launch {
            stayRepository.watchAllUpcomingCheckInsAndCheckOuts().collect { result ->
                result
                    .onError { error ->
                        if (error.isRedirectError()) {
                            userRepository.deleteCachedUser()
                            _uiEvents.send(UiEvents.RedirectToSignIn)
                        }

                        _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                    }
                    .onSuccess { upcomingCheckInsAndCheckOuts ->
                        val todayCheckInsAndCheckoutsCount = upcomingCheckInsAndCheckOuts.count {
                            val canRegisterCheckIn = !it.isCheckInDone
                                    && now.isHigherThan(it.checkInDateTime.toStartOfDay())
                            val canRegisterCheckOut =
                                now.isHigherThan(it.checkOutDateTime.toStartOfDay())

                            canRegisterCheckIn || canRegisterCheckOut
                        }

                        _todayCheckInsAndCheckoutsCount.update { todayCheckInsAndCheckoutsCount }
                    }
            }
        }
    }

    fun deleteCurrentAssistance(
        currentAssistance: Assistance,
        leadTenant: Tenant,
    ) {
        viewModelScope.launch {
            deleteCurrentAssistanceUseCase(
                currentAssistance = currentAssistance,
                hasFinishedStays = leadTenant.hasFinishedStays(),
            )
                .onError { error ->
                    if (error.isRedirectError()) {
                        userRepository.deleteCachedUser()
                        _uiEvents.send(UiEvents.RedirectToSignIn)
                    }

                    _uiEvents.send(UiEvents.DataSourceError(messageId = error.toUiMessage()))
                    return@launch
                }

            _uiEvents.send(UiEvents.CurrentAssistanceDeleteSuccess)
        }
    }

    sealed interface UiEvents {
        data class DataSourceError(@StringRes val messageId: Int) : UiEvents

        data object RedirectToSignIn : UiEvents

        data object CurrentAssistanceDeleteSuccess : UiEvents
    }
}
