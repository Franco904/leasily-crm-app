package com.fstengineering.leasilycrm.core.application.services

import com.fstengineering.leasilycrm.core.application.repositories.AuthRepository
import com.fstengineering.leasilycrm.core.application.services.interfaces.UserSessionService
import com.fstengineering.leasilycrm.core.application.services.interfaces.UserSessionService.UserSessionEvent
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class UserSessionServiceImpl(
    private val authRepository: AuthRepository,
) : UserSessionService {
    private val _userSessionEvents = MutableSharedFlow<UserSessionEvent>()
    override val userSessionEvents: Flow<UserSessionEvent> = _userSessionEvents.asSharedFlow()

    override suspend fun onUserInitiatedSignOut(): AppResult<Unit, DataError> {
        return authRepository.signOut()
            .onSuccess { _userSessionEvents.emit(UserSessionEvent.UserInitiatedSignOutEvent) }
    }

    override suspend fun onSessionExpired(): AppResult<Unit, DataError> {
        return authRepository.signOut()
            .onSuccess { _userSessionEvents.emit(UserSessionEvent.SessionExpiredEvent) }
    }
}
