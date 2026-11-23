package com.fstengineering.leasilycrm.core.infra.stores

import com.fstengineering.leasilycrm.core.application.models.UserRetryInfoWriteModel
import com.fstengineering.leasilycrm.core.application.stores.UserSyncStore
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.infra.localStorage.encrypted.EncryptedPreferences
import com.fstengineering.leasilycrm.core.infra.localStorage.interfaces.LocalStorage
import com.fstengineering.leasilycrm.core.infra.api.interfaces.UserApi
import com.fstengineering.leasilycrm.core.infra.api.requestDtos.user.UpdateUserDto
import com.fstengineering.leasilycrm.core.infra.projections.SyncState
import kotlinx.coroutines.flow.firstOrNull

class UserSyncStoreImpl(
    private val remoteDataSource: UserApi,
    private val encryptedLocalStorage: LocalStorage<EncryptedPreferences>,
) : UserSyncStore {
    override suspend fun retryUpdate(): AppResult<Unit> {
        val prefs = encryptedLocalStorage.readPreferences()
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { prefsFlow -> prefsFlow.firstOrNull() },
            )

        val syncStateId = prefs?.userSyncStateId
        val writeModel = UserRetryInfoWriteModel(prefs?.creciNumber)

        if (syncStateId != SyncState.PendingSync.id) {
            return AppResult.Success(data = Unit) // no update needed
        }

        remoteDataSource.updateUser(updateUserDto = UpdateUserDto(writeModel.creci))
            .onError { remoteBackendError ->
                encryptedLocalStorage.savePreferences { prefs ->
                    prefs.copy(userSyncStateId = SyncState.PendingSync.id)
                }
                    .onError { return AppResult.Error(error = it) }

                return AppResult.Error(error = remoteBackendError)
            }
            .onSuccess {
                encryptedLocalStorage.savePreferences { prefs ->
                    prefs.copy(userSyncStateId = SyncState.Synced.id)
                }
                    .onError { return AppResult.Error(error = it) }
            }

        return AppResult.Success(data = Unit)
    }
}
