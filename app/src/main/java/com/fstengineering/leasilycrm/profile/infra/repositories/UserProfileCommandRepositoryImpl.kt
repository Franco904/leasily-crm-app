package com.fstengineering.leasilycrm.profile.infra.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.encrypted.EncryptedPreferences
import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.interfaces.PreferencesDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.remote.interfaces.UserRemoteDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.remote.requestDtos.user.UpdateUserDto
import com.fstengineering.leasilycrm.core.infra.models.SyncState
import com.fstengineering.leasilycrm.profile.application.repositories.UserProfileCommandRepository
import com.fstengineering.leasilycrm.profile.domain.entities.User

class UserProfileCommandRepositoryImpl(
    private val remoteDataSource: UserRemoteDataSource,
    private val encryptedPreferencesDataSource: PreferencesDataSource<EncryptedPreferences>,
) : UserProfileCommandRepository {
    override suspend fun update(user: User): AppResult<Unit, ApplicationError> {
        val creciNumber = user.creci?.value

        encryptedPreferencesDataSource.savePreferences { prefs ->
            prefs.copy(creciNumber = creciNumber)
        }
            .onError { return AppResult.Error(error = it.toApplicationError()) }

        val dto = UpdateUserDto(creciNumber = creciNumber)

        remoteDataSource.updateUser(updateUserDto = dto)
            .onError { e ->
                encryptedPreferencesDataSource.savePreferences { prefs ->
                    prefs.copy(userSyncStateId = SyncState.PendingSync.id)
                }

                return AppResult.Error(error = e.toApplicationError())
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
