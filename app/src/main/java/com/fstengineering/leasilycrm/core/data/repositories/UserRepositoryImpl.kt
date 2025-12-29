package com.fstengineering.leasilycrm.core.data.repositories

import com.fstengineering.leasilycrm.core.data.adapters.interfaces.ConnectivityAdapter
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.encrypted.EncryptedPreferences
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.interfaces.PreferencesStorage
import com.fstengineering.leasilycrm.core.data.dataSources.remoteBackend.apis.interfaces.UserApi
import com.fstengineering.leasilycrm.core.data.dataSources.remoteBackend.requestDtos.user.SignInRequestDto
import com.fstengineering.leasilycrm.core.data.dataSources.remoteBackend.requestDtos.user.UpdateUserDto
import com.fstengineering.leasilycrm.core.data.models.SyncState
import com.fstengineering.leasilycrm.core.domain.models.User
import com.fstengineering.leasilycrm.core.domain.repositories.UserRepository
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
    private val connectivityAdapter: ConnectivityAdapter,
    private val api: UserApi,
    private val encryptedPreferencesStorage: PreferencesStorage<EncryptedPreferences>,
) : UserRepository {
    override suspend fun isUserSignedIn(): AppResult<Boolean, DataError> {
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

    override suspend fun signInUser(
        idToken: String,
        user: User,
    ): AppResult<Unit, DataError> {
        val requestDto = SignInRequestDto(idToken = idToken)
        val remoteUserId = api.signInUser(signInRequestDto = requestDto)
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { responseDto -> responseDto.id }
            )

        encryptedPreferencesStorage.savePreferences { prefs ->
            prefs.copy(
                remoteId = remoteUserId,
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

    override suspend fun getRemoteUser(): AppResult<User, DataError> {
        if (!isDeviceConnectedToInternet()) {
            return AppResult.Error(error = DataError.NoInternetError)
        }

        val fetchedUser = api.getUser()
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
    ): AppResult<Unit, DataError> {
        // Cache new user name
        encryptedPreferencesStorage.savePreferences { prefs ->
            prefs.copy(
                name = user.name,
                creciNumber = user.creciNumber,
            )
        }
            .onError { return AppResult.Error(error = it) }

        // Try to send new user name to remote backend API
        val updateUserDto = UpdateUserDto(
            name = user.name!!,
            creciNumber = user.creciNumber,
        )

        api.updateUser(updateUserDto = updateUserDto)
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

    override suspend fun deleteCachedUser(): AppResult<Unit, DataError> {
        return encryptedPreferencesStorage.savePreferences { prefs ->
            prefs.copy(
                sessionCookie = null,
                remoteId = null,
                name = null,
                creciNumber = null,
                profilePictureUrl = null,
            )
        }
    }

    override suspend fun retryUserRemoteUpdate(): AppResult<Unit, DataError> {
        val prefs = encryptedPreferencesStorage.readPreferences()
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { prefsFlow -> prefsFlow.first() },
            )

        if (prefs.sessionCookie == null || prefs.userSyncStateId != SyncState.Dirty.id) {
            return AppResult.Success(data = Unit) // no update needed
        }

        val cachedUser = prefs.toUser()
        val updateUserDto = UpdateUserDto(
            name = cachedUser.name!!,
            creciNumber = cachedUser.creciNumber,
        )

        api.updateUser(updateUserDto = updateUserDto)
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

    override suspend fun signOutUser(): AppResult<Unit, DataError> {
        if (!connectivityAdapter.isDeviceConnectedToInternet(canUseMobile = true)) {
            return AppResult.Error(error = DataError.NoInternetError)
        }

        api.signOutUser()
            .onError { return AppResult.Error(error = it) }

        encryptedPreferencesStorage.savePreferences { prefs ->
            prefs.copy(
                sessionCookie = null,
                remoteId = null,
                name = null,
                creciNumber = null,
                profilePictureUrl = null,
            )
        }
            .onError { return AppResult.Error(error = it) }

        return AppResult.Success(data = Unit)
    }

    override fun isDeviceConnectedToInternet(): Boolean {
        return connectivityAdapter.isDeviceConnectedToInternet(canUseMobile = true)
    }
}
