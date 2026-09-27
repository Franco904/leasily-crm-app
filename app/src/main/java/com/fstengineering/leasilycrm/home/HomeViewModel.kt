package com.fstengineering.leasilycrm.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fstengineering.leasilycrm.core.application.repositories.OccupiedPeriodSharedQueryRepository
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.home.application.repositories.UserQueryRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val occupiedPeriodSharedQueryRepository: OccupiedPeriodSharedQueryRepository,
    private val userQueryRepository: UserQueryRepository,
) : ViewModel() {
    private var isInitialized = false

    private val _uiEvents = Channel<UiEvent>(Channel.BUFFERED)
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

    private fun watchUserProfilePictureUrl() {
        viewModelScope.launch {
            userQueryRepository.watchUserProfilePictureUrl().collect { result ->
                result
                    .onError { e -> _uiEvents.trySend(UiEvent.BottomSheetError(e)) }
                    .onSuccess { picUrl -> _userProfilePictureUrl.value = picUrl }
            }
        }
    }

    private fun watchTodayCheckInsAndCheckouts() {
        viewModelScope.launch {
            occupiedPeriodSharedQueryRepository.watchAllPastCheckInCheckOutNotDoneCounts()
                .collect { result ->
                    result
                        .onError { e -> _uiEvents.trySend(UiEvent.BottomSheetError(e)) }
                        .onSuccess { counts ->
                            val todayCheckInsAndCheckOutsCount =
                                counts.staysAbleToCheckInCount + counts.staysAbleToCheckOutCount

                            _todayCheckInsAndCheckoutsCount.update { todayCheckInsAndCheckOutsCount }
                        }
                }
        }
    }

    override fun onCleared() {
        _uiEvents.close()

        super.onCleared()
    }

    sealed interface UiEvent {
        data class BottomSheetError(val error: ApplicationError) : UiEvent
    }
}
