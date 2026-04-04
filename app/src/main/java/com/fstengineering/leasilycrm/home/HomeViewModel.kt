package com.fstengineering.leasilycrm.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.assistanceRegistration.application.repositories.AssistanceCommandRepository
import com.fstengineering.leasilycrm.assistanceRegistration.domain.entities.Assistance
import com.fstengineering.leasilycrm.core.application.repositories.StaySharedCommandRepository
import com.fstengineering.leasilycrm.core.application.stores.UserSyncStore
import com.fstengineering.leasilycrm.core.domain.helpers.AppDateTime
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.infra.utils.InfraError
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val assistanceCommandRepository: AssistanceCommandRepository,
    private val staySharedCommandRepository: StaySharedCommandRepository,
    private val userQueryRepository: UserSyncStore,
    private val now: AppDateTime,
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

        watchUserProfilePictureUrl()
        watchTodayCheckInsAndCheckouts()

        isInitialized = true
    }

    fun watchUserProfilePictureUrl() {
        viewModelScope.launch {
            userQueryRepository.watch().collect { result ->
                result
                    .onError { e -> _uiEvents.send(UiEvents.Error(e)) }
                    .onSuccess { user -> _userProfilePictureUrl.value = user.profilePictureUrl }
            }
        }
    }

    private fun watchTodayCheckInsAndCheckouts() {
        viewModelScope.launch {
            staySharedCommandRepository.watchAllUpcomingCheckInsAndCheckOuts().collect { result ->
                result
                    .onError { e -> _uiEvents.send(UiEvents.Error(e)) }
                    .onSuccess { upcomingCheckInsAndCheckOuts ->
                        val todayCheckInsAndCheckoutsCount = upcomingCheckInsAndCheckOuts.count {
                            val canRegisterCheckIn = !it.isCheckInDone
                                    && now.isAfter(it.checkInDateTime.toStartOfDay())
                            val canRegisterCheckOut =
                                now.isAfter(it.checkOutDateTime.toStartOfDay())

                            canRegisterCheckIn || canRegisterCheckOut
                        }

                        _todayCheckInsAndCheckoutsCount.update { todayCheckInsAndCheckoutsCount }
                    }
            }
        }
    }

    fun deleteCurrentAssistance(currentAssistance: Assistance) {
        viewModelScope.launch {
            assistanceCommandRepository.delete(id = currentAssistance.id!!)
                .onError { e -> return@launch _uiEvents.send(UiEvents.Error(e)) }

            _uiEvents.send(UiEvents.CurrentAssistanceDeleteSuccess)
        }
    }

    sealed interface UiEvents {
        data class Error(val error: InfraError) : UiEvents

        data object CurrentAssistanceDeleteSuccess : UiEvents
    }
}
