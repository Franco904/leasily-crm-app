package com.fstengineering.leasilycrm.core.domain.repositories

import com.fstengineering.leasilycrm.core.domain.models.User
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun isUserSignedIn(): AppResult<Boolean, DataSourceError>

    suspend fun didUserExplicitlySignOut(): AppResult<Boolean, DataSourceError>

    suspend fun signInUser(
        idToken: String,
        user: User,
    ): AppResult<Unit, DataSourceError>

    fun watchUser(): Flow<AppResult<User, DataSourceError>>

    suspend fun updateUser(user: User): AppResult<Unit, DataSourceError>

    suspend fun retrySendUserUpdate(): AppResult<Unit, DataSourceError>

    suspend fun deleteUser(): AppResult<Unit, DataSourceError>

    suspend fun signOutUser(): AppResult<Unit, DataSourceError>
}
