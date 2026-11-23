package com.fstengineering.leasilycrm.core.application.services

import com.fstengineering.leasilycrm.core.application.stores.UserSessionSharedStore
import com.fstengineering.leasilycrm.core.infra.api.interfaces.UserApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class UserSessionService(
    private val userSessionSharedStore: UserSessionSharedStore,
    private val userApi: UserApi,
) {
    private val _userSessionEvents = MutableSharedFlow<UserSessionEvent>()
    val userSessionEvents: Flow<UserSessionEvent> = _userSessionEvents.asSharedFlow()

    /**
     * Encerra a sessão no back-end como best-effort (falha ignorada: a sessão local é encerrada
     * de qualquer forma) e, em seguida, no dispositivo.
     */
    suspend fun onUserInitiatedSignOut() {
        userApi.signOutUser()
        endSession(event = UserSessionEvent.UserInitiatedSignOutEvent)
    }

    /**
     * Encerra a sessão só no dispositivo: o back-end já a considera inválida, e acioná-lo de novo
     * resultaria em outro 401/403.
     */
    suspend fun onSessionExpired() {
        endSession(event = UserSessionEvent.SessionExpiredEvent)
    }

    private suspend fun endSession(event: UserSessionEvent) {
        userSessionSharedStore.clearSessionState()
        _userSessionEvents.emit(event)
    }

    sealed interface UserSessionEvent {
        data object UserInitiatedSignOutEvent : UserSessionEvent
        data object SessionExpiredEvent : UserSessionEvent
    }
}
