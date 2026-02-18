package com.fstengineering.leasilycrm.core.infra.repositories

import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.encrypted.EncryptedPreferences
import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.interfaces.PreferencesDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.remote.interfaces.UserRemoteDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.remote.requestDtos.user.UpdateUserDto
import com.fstengineering.leasilycrm.core.infra.models.SyncState
import com.fstengineering.leasilycrm.core.domain.entities.User
import com.fstengineering.leasilycrm.core.application.repositories.UserRepository
import com.fstengineering.leasilycrm.core.application.services.interfaces.UserSessionService
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class UserRepositoryImpl(
    private val userSessionService: UserSessionService,
    private val remoteDataSource: UserRemoteDataSource,
    private val encryptedPreferencesDataSource: PreferencesDataSource<EncryptedPreferences>,
) : UserRepository {
    override suspend fun storeUser(user: User): AppResult<Unit, DataError> {
        encryptedPreferencesDataSource.savePreferences { prefs ->
            prefs.copy(
                remoteId = user.remoteId,
                name = user.name,
                creciNumber = user.creciNumber,
                profilePictureUrl = user.profilePictureUrl,
                userSyncStateId = user.userSyncStateId,
            )
        }
            .onError { return AppResult.Error(error = it) }

        return AppResult.Success(data = Unit)
    }

    override fun watchUser(): Flow<AppResult<User, DataError>> = flow {
        encryptedPreferencesDataSource.readPreferences()
            .onError { emit(AppResult.Error(error = it)) }
            .onSuccess { prefsFlow ->
                emitAll(
                    prefsFlow
                        .map { AppResult.Success<User, DataError>(data = it.toUser()) }
                        .distinctUntilChanged()
                )
            }
    }

    override suspend fun updateUserCreciNumber(
        creciNumber: String?,
    ): AppResult<Unit, DataError> {
        encryptedPreferencesDataSource.savePreferences { prefs ->
            prefs.copy(creciNumber = creciNumber)
        }
            .onError { return AppResult.Error(error = it) }

        val dto = UpdateUserDto(creciNumber = creciNumber)
        remoteDataSource.updateUser(updateUserDto = dto)
            .onError { e ->
                encryptedPreferencesDataSource.savePreferences { prefs ->
                    prefs.copy(userSyncStateId = SyncState.PendingSync.id)
                }

                if (e == DataError.UnauthorizedError) {
                    userSessionService.onSessionExpired()
                }

                return AppResult.Error(error = e)
            }
            .onSuccess {
                encryptedPreferencesDataSource.savePreferences { prefs ->
                    prefs.copy(userSyncStateId = SyncState.Synced.id)
                }
                    .onError { return AppResult.Error(error = it) }
            }

        return AppResult.Success(data = Unit)
    }

    override suspend fun retryUserUpdate(): AppResult<Unit, DataError> {
        val prefs = encryptedPreferencesDataSource.readPreferences()
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { prefsFlow -> prefsFlow.firstOrNull() },
            )

        if (prefs?.userSyncStateId != SyncState.PendingSync.id) {
            return AppResult.Success(data = Unit) // no update needed
        }

        val cachedUser = prefs.toUser()
        remoteDataSource.updateUser(updateUserDto = UpdateUserDto(cachedUser.creciNumber))
            .onError { remoteBackendError ->
                encryptedPreferencesDataSource.savePreferences { prefs ->
                    prefs.copy(userSyncStateId = SyncState.PendingSync.id)
                }
                    .onError { return AppResult.Error(error = it) }

                return AppResult.Error(error = remoteBackendError)
            }
            .onSuccess {
                encryptedPreferencesDataSource.savePreferences { prefs ->
                    prefs.copy(userSyncStateId = SyncState.Synced.id)
                }
                    .onError { return AppResult.Error(error = it) }
            }

        return AppResult.Success(data = Unit)
    }
}
