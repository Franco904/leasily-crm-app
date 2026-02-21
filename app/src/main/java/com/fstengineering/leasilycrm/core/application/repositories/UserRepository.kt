package com.fstengineering.leasilycrm.core.application.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.entities.User
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun storeUser(user: User): AppResult<Unit, ApplicationError>

    fun watchUser(): Flow<AppResult<User, ApplicationError>>

    suspend fun updateUserCreciNumber(creciNumber: String?): AppResult<Unit, ApplicationError>

    suspend fun retryUserUpdate(): AppResult<Unit, ApplicationError>
}
