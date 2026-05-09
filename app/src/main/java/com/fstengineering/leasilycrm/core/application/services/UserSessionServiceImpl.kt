package com.fstengineering.leasilycrm.core.application.services

import com.fstengineering.leasilycrm.core.application.gateways.UserSessionSharedGateway
import com.fstengineering.leasilycrm.core.application.services.interfaces.UserSessionService
import com.fstengineering.leasilycrm.core.application.services.interfaces.UserSessionService.UserSessionEvent
import com.fstengineering.leasilycrm.core.application.stores.UserSessionSharedStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class UserSessionServiceImpl(
    private val userSessionSharedGateway: UserSessionSharedGateway,
    private val userSessionSharedStore: UserSessionSharedStore,
) : UserSessionService {
    private val _userSessionEvents = MutableSharedFlow<UserSessionEvent>()
    override val userSessionEvents: Flow<UserSessionEvent> = _userSessionEvents.asSharedFlow()

    override suspend fun onUserInitiatedSignOut() {
        userSessionSharedGateway.killSession()
        userSessionSharedStore.clearSessionState()

        _userSessionEvents.emit(UserSessionEvent.UserInitiatedSignOutEvent)
    }

    override suspend fun onSessionExpired() {
        userSessionSharedGateway.killSession()
        userSessionSharedStore.clearSessionState()

        _userSessionEvents.emit(UserSessionEvent.SessionExpiredEvent)
    }
}
