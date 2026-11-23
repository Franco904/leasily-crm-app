package com.fstengineering.leasilycrm.profile.infra.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.infra.localStorage.encrypted.EncryptedPreferences
import com.fstengineering.leasilycrm.core.infra.localStorage.interfaces.LocalStorage
import com.fstengineering.leasilycrm.core.infra.api.interfaces.UserApi
import com.fstengineering.leasilycrm.core.infra.api.requestDtos.user.UpdateUserDto
import com.fstengineering.leasilycrm.core.infra.projections.SyncState
import com.fstengineering.leasilycrm.profile.application.repositories.UserCommandRepository
import com.fstengineering.leasilycrm.profile.domain.entities.User

class UserCommandRepositoryImpl(
    private val remoteDataSource: UserApi,
    private val encryptedLocalStorage: LocalStorage<EncryptedPreferences>,
) : UserCommandRepository {
    override suspend fun update(user: User): AppResult<Unit> {
        val creciNumber = user.creci?.value

        encryptedLocalStorage.savePreferences { prefs ->
            prefs.copy(creciNumber = creciNumber)
        }
            .onError { return AppResult.Error(error = it) }

        val dto = UpdateUserDto(creciNumber = creciNumber)

        remoteDataSource.updateUser(updateUserDto = dto)
            .onError { e ->
                encryptedLocalStorage.savePreferences { prefs ->
                    prefs.copy(userSyncStateId = SyncState.PendingSync.id)
                }

                return AppResult.Error(error = e)
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
