package com.fstengineering.leasilycrm.core.data.repositories

import com.fstengineering.leasilycrm.core.data.dataSources.preferences.encrypted.EncryptedPreferences
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.interfaces.PreferencesStorage
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.plain.PlainPreferences
import com.fstengineering.leasilycrm.core.data.dataSources.remoteBackend.apis.interfaces.UserApi
import com.fstengineering.leasilycrm.core.data.dataSources.remoteBackend.requestDtos.SignInRequestDto
import com.fstengineering.leasilycrm.core.data.dataSources.remoteBackend.requestDtos.UpdateUserRequestDto
import com.fstengineering.leasilycrm.core.data.models.SyncState
import com.fstengineering.leasilycrm.core.domain.models.User
import com.fstengineering.leasilycrm.core.domain.repositories.UserRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError
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
    private val userApi: UserApi,
    private val encryptedPreferencesStorage: PreferencesStorage<EncryptedPreferences>,
    private val plainPreferencesStorage: PreferencesStorage<PlainPreferences>,
) : UserRepository {
    override suspend fun isUserSignedIn(): AppResult<Boolean, DataSourceError> {
        return encryptedPreferencesStorage.readPreferences()
            .fold(
                onError = { error ->
                    AppResult.Error(error = error)
                },
                onSuccess = { prefsFlow ->
                    val isSignedIn = prefsFlow.first().sessionCookie != null

                    AppResult.Success(data = isSignedIn)
                }
            )
    }

    override suspend fun didUserExplicitlySignOut(): AppResult<Boolean, DataSourceError> {
        return plainPreferencesStorage.readPreferences()
            .fold(
                onError = { error ->
                    AppResult.Error(error = error)
                },
                onSuccess = { prefsFlow ->
                    val didUserExplicitlySignOut =
                        prefsFlow.first().didUserExplicitlySignOut != null

                    AppResult.Success(data = didUserExplicitlySignOut)
                }
            )
    }

    override suspend fun signInUser(
        idToken: String,
        user: User,
    ): AppResult<Unit, DataSourceError> {
        val requestDto = SignInRequestDto(idToken = idToken)
        userApi.signInUser(signInRequestDto = requestDto)
            .onError { return AppResult.Error(error = it) }

        encryptedPreferencesStorage.savePreferences { prefs ->
            prefs.copy(
                name = user.name,
                creciNumber = user.creciNumber,
                profilePictureUrl = user.profilePictureUrl,
                userSyncStateId = SyncState.Clean.id,
            )
        }
            .onError { return AppResult.Error(error = it) }

        plainPreferencesStorage.savePreferences { prefs ->
            prefs.copy(
                didUserExplicitlySignOut = false,
                hasSessionExpired = false,
            )
        }
            .onError { return AppResult.Error(error = it) }

        return AppResult.Success(data = Unit)
    }

    override suspend fun watchSessionExpiration(): Flow<AppResult<Boolean, DataSourceError>> =
        flow {
            plainPreferencesStorage.readPreferences()
                .onError { emit(AppResult.Error(error = it)) }
                .onSuccess { prefsFlow ->
                    val hasSessionExpiredFlow = prefsFlow.map {
                        AppResult.Success<Boolean, DataSourceError>(data = it.hasSessionExpired == true)
                    }

                    emitAll(hasSessionExpiredFlow)
                }
        }

    override suspend fun markSessionAsExpired(): AppResult<Unit, DataSourceError> {
        return plainPreferencesStorage.savePreferences {
            it.copy(hasSessionExpired = true)
        }
    }

    override fun watchUser(): Flow<AppResult<User, DataSourceError>> = flow {
        encryptedPreferencesStorage.readPreferences()
            .onError { emit(AppResult.Error(error = it)) }
            .onSuccess { prefsFlow ->
                val prefs = prefsFlow.first()

                if (prefs.sessionCookie == null) {
                    emit(AppResult.Error(error = DataSourceError.RemoteBackendError.Unauthorized))
                    return@flow
                }

                emitAll(
                    prefsFlow
                        .map { AppResult.Success<User, DataSourceError>(data = it.toUser()) }
                        .distinctUntilChanged()
                )
            }
    }

    override suspend fun getRemoteUser(): AppResult<User, DataSourceError> {
        val fetchedUser = userApi.getUser()
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { responseDto -> responseDto.toUser() }
            )

        encryptedPreferencesStorage.savePreferences { prefs ->
            prefs.copy(
                name = fetchedUser.name,
                creciNumber = fetchedUser.creciNumber,
                profilePictureUrl = fetchedUser.profilePictureUrl,
            )
        }
            .onError { return AppResult.Error(error = it) }

        return AppResult.Success(data = fetchedUser)
    }

    override suspend fun updateUser(
        user: User,
    ): AppResult<Unit, DataSourceError> {
        // Cache new user name
        encryptedPreferencesStorage.savePreferences { prefs ->
            prefs.copy(
                name = user.name,
                creciNumber = user.creciNumber,
            )
        }
            .onError { return AppResult.Error(error = it) }

        // Try to send new user name to remote backend API
        val updateUserRequestDto = UpdateUserRequestDto(
            name = user.name!!,
            creciNumber = user.creciNumber,
        )

        userApi.updateUser(updateUserRequestDto = updateUserRequestDto)
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

    override suspend fun deleteCachedUser(): AppResult<Unit, DataSourceError> {
        return encryptedPreferencesStorage.savePreferences { prefs ->
            prefs.copy(
                sessionCookie = null,
                name = null,
                creciNumber = null,
                profilePictureUrl = null,
            )
        }
    }

    override suspend fun retryUserRemoteUpdate(): AppResult<Unit, DataSourceError> {
        val prefs = encryptedPreferencesStorage.readPreferences()
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { prefsFlow -> prefsFlow.first() },
            )

        if (prefs.sessionCookie == null || prefs.userSyncStateId != SyncState.Dirty.id) {
            return AppResult.Success(data = Unit) // no update needed
        }

        val cachedUser = prefs.toUser()
        val updateUserRequestDto = UpdateUserRequestDto(
            name = cachedUser.name!!,
            creciNumber = cachedUser.creciNumber,
        )

        userApi.updateUser(updateUserRequestDto = updateUserRequestDto)
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

    override suspend fun signOutUser() = userApi.signOutUser()
}
