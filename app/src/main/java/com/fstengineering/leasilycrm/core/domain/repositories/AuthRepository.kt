package com.fstengineering.leasilycrm.core.domain.repositories

import com.fstengineering.leasilycrm.core.domain.models.User
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError

interface AuthRepository {
    suspend fun isSessionAlive(): AppResult<Boolean, DataError>

    suspend fun signIn(idToken: String): AppResult<User, DataError>

    suspend fun signOut(): AppResult<Unit, DataError>

    fun checkDeviceIsConnectedToInternet(): AppResult<Unit, DataError>
}
