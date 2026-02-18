package com.fstengineering.leasilycrm.core.application.services.interfaces

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import kotlinx.coroutines.flow.Flow

interface UserSessionService {
    val userSessionEvents: Flow<UserSessionEvent>

    suspend fun onUserInitiatedSignOut(): AppResult<Unit, DataError>

    suspend fun onSessionExpired(): AppResult<Unit, DataError>

    sealed interface UserSessionEvent {
        data object UserInitiatedSignOutEvent : UserSessionEvent
        data object SessionExpiredEvent : UserSessionEvent
    }
}
