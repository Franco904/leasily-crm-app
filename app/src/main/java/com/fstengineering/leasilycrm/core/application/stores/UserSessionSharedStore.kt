package com.fstengineering.leasilycrm.core.application.stores

interface UserSessionSharedStore {
    suspend fun isSessionAlive(): Boolean

    suspend fun clearSessionState()
}
