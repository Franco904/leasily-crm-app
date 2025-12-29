package com.fstengineering.leasilycrm.core.data.repositories

import com.fstengineering.leasilycrm.core.data.dataSources.preferences.encrypted.EncryptedPreferences
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.interfaces.PreferencesStorage
import com.fstengineering.leasilycrm.core.data.dataSources.remoteBackend.apis.interfaces.UserApi
import com.fstengineering.leasilycrm.core.data.dataSources.remoteBackend.requestDtos.user.UpdateUserDto
import com.fstengineering.leasilycrm.core.data.models.SyncState
import com.fstengineering.leasilycrm.core.domain.models.User
import com.fstengineering.leasilycrm.core.domain.repositories.UserRepository
import com.fstengineering.leasilycrm.core.domain.services.interfaces.UserSessionService
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class UserRepositoryImpl(
    private val api: UserApi,
    private val encryptedPreferencesStorage: PreferencesStorage<EncryptedPreferences>,
    private val userSessionService: UserSessionService,
) : UserRepository {
    override suspend fun storeUser(user: User): AppResult<Unit, DataError> {
        encryptedPreferencesStorage.savePreferences { prefs ->
            prefs.copy(
                remoteId = user.remoteId,
                name = user.name,
                creciNumber = user.creciNumber,
                profilePictureUrl = user.profilePictureUrl,
                userSyncStateId = SyncState.Clean.id,
            )
        }
            .onError { return AppResult.Error(error = it) }

        return AppResult.Success(data = Unit)
    }

    override fun watchUser(): Flow<AppResult<User, DataError>> = flow {
        encryptedPreferencesStorage.readPreferences()
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
        // Cache new user name
        encryptedPreferencesStorage.savePreferences { prefs ->
            prefs.copy(creciNumber = creciNumber)
        }
            .onError { return AppResult.Error(error = it) }

        // Try to send new user name to remote backend API
        api.updateUser(updateUserDto = UpdateUserDto(creciNumber = creciNumber))
            .onError { e ->
                encryptedPreferencesStorage.savePreferences { prefs ->
                    prefs.copy(userSyncStateId = SyncState.Dirty.id)
                }

                if (e is DataError.UnauthorizedError) userSessionService.onSessionExpired()

                return AppResult.Error(error = e)
            }
            .onSuccess {
                encryptedPreferencesStorage.savePreferences { prefs ->
                    prefs.copy(userSyncStateId = SyncState.Clean.id)
                }
                    .onError { return AppResult.Error(error = it) }
            }

        return AppResult.Success(data = Unit)
    }

    override suspend fun retryUserUpdate(): AppResult<Unit, DataError> {
        val prefs = encryptedPreferencesStorage.readPreferences()
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { prefsFlow -> prefsFlow.first() },
            )

        if (prefs.userSyncStateId != SyncState.Dirty.id) {
            return AppResult.Success(data = Unit) // no update needed
        }

        val cachedUser = prefs.toUser()
        api.updateUser(updateUserDto = UpdateUserDto(cachedUser.creciNumber))
            .onError { remoteBackendError ->
                encryptedPreferencesStorage.savePreferences { prefs ->
                    prefs.copy(userSyncStateId = SyncState.Dirty.id)
                }
                    .onError { return AppResult.Error(error = it) }

                return AppResult.Error(error = remoteBackendError)
            }
            .onSuccess {
                encryptedPreferencesStorage.savePreferences { prefs ->
                    prefs.copy(userSyncStateId = SyncState.Clean.id)
                }
                    .onError { return AppResult.Error(error = it) }
            }

        return AppResult.Success(data = Unit)
    }
}
