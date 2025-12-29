package com.fstengineering.leasilycrm.core.domain.repositories

import com.fstengineering.leasilycrm.core.domain.models.User
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun storeUser(user: User): AppResult<Unit, DataError>

    fun watchUser(): Flow<AppResult<User, DataError>>

    suspend fun updateUserCreciNumber(creciNumber: String?): AppResult<Unit, DataError>

    suspend fun retryUserUpdate(): AppResult<Unit, DataError>
}
