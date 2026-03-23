package com.fstengineering.leasilycrm.core.application.services.interfaces

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import kotlinx.coroutines.flow.Flow

interface UserSessionService {
    val userSessionEvents: Flow<UserSessionEvent>

    suspend fun onUserInitiatedSignOut()

    suspend fun onSessionExpired()

    sealed interface UserSessionEvent {
        data object UserInitiatedSignOutEvent : UserSessionEvent
        data object SessionExpiredEvent : UserSessionEvent
    }
}
