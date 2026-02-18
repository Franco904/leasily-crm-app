package com.fstengineering.leasilycrm.core.application.repositories

import com.fstengineering.leasilycrm.core.domain.entities.User
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError

interface AuthRepository {
    suspend fun isSessionAlive(): AppResult<Boolean, DataError>

    suspend fun signIn(idToken: String): AppResult<User, DataError>

    suspend fun signOut(): AppResult<Unit, DataError>

    fun checkDeviceIsConnectedToInternet(): AppResult<Unit, DataError>
}
