package com.fstengineering.leasilycrm.core.domain.repositories

import com.fstengineering.leasilycrm.core.domain.models.User
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun isUserSignedIn(): AppResult<Boolean, DataError>

    suspend fun signInUser(
        idToken: String,
        user: User,
    ): AppResult<Unit, DataError>

    fun watchUser(): Flow<AppResult<User, DataError>>

    suspend fun updateUser(user: User): AppResult<Unit, DataError>

    suspend fun getRemoteUser(): AppResult<User, DataError>

    suspend fun deleteCachedUser(): AppResult<Unit, DataError>

    suspend fun retryUserRemoteUpdate(): AppResult<Unit, DataError>

    suspend fun signOutUser(): AppResult<Unit, DataError>

    fun isDeviceConnectedToInternet(): Boolean
}
