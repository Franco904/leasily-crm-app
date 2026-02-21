package com.fstengineering.leasilycrm.core.application.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.entities.User
import com.fstengineering.leasilycrm.core.domain.utils.AppResult

interface AuthRepository {
    suspend fun isSessionAlive(): AppResult<Boolean, ApplicationError>

    suspend fun signIn(idToken: String): AppResult<User, ApplicationError>

    suspend fun signOut(): AppResult<Unit, ApplicationError>

    fun checkDeviceIsConnectedToInternet(): AppResult<Unit, ApplicationError>
}
