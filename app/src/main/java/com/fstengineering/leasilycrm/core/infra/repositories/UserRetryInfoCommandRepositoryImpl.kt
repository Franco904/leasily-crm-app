package com.fstengineering.leasilycrm.core.infra.repositories

import com.fstengineering.leasilycrm.core.application.repositories.UserRetryInfoCommandRepository
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.entities.UserRetryInfo
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.encrypted.EncryptedPreferences
import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.interfaces.PreferencesDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.remote.interfaces.UserRemoteDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.remote.requestDtos.user.UpdateUserDto
import com.fstengineering.leasilycrm.core.infra.models.SyncState
import kotlinx.coroutines.flow.firstOrNull

class UserRetryInfoCommandRepositoryImpl(
    private val remoteDataSource: UserRemoteDataSource,
    private val encryptedPreferencesDataSource: PreferencesDataSource<EncryptedPreferences>,
) : UserRetryInfoCommandRepository {
    override suspend fun retryUpdate(): AppResult<Unit, ApplicationError> {
        val prefs = encryptedPreferencesDataSource.readPreferences()
            .fold(
                onError = { return AppResult.Error(error = it.toApplicationError()) },
                onSuccess = { prefsFlow -> prefsFlow.firstOrNull() },
            )

        val syncStateId = prefs?.userSyncStateId
        val user = UserRetryInfo(prefs?.creciNumber)

        if (syncStateId != SyncState.PendingSync.id) {
            return AppResult.Success(data = Unit) // no update needed
        }

        remoteDataSource.updateUser(updateUserDto = UpdateUserDto(user.creci))
            .onError { remoteBackendError ->
                encryptedPreferencesDataSource.savePreferences { prefs ->
                    prefs.copy(userSyncStateId = SyncState.PendingSync.id)
                }
                    .onError { return AppResult.Error(error = it.toApplicationError()) }

                return AppResult.Error(error = remoteBackendError.toApplicationError())
            }
            .onSuccess {
                encryptedPreferencesDataSource.savePreferences { prefs ->
                    prefs.copy(userSyncStateId = SyncState.Synced.id)
                }
                    .onError { return AppResult.Error(error = it.toApplicationError()) }
            }

        return AppResult.Success(data = Unit)
    }
}
