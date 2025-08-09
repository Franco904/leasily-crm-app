package com.fstengineering.leasilycrm.core.data.repositories

import com.fstengineering.leasilycrm.core.data.dataSources.preferences.encrypted.EncryptedPreferences
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.interfaces.PreferencesStorage
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.plain.PlainPreferences
import com.fstengineering.leasilycrm.core.data.dataSources.remoteBackend.apis.interfaces.UserApi
import com.fstengineering.leasilycrm.core.data.dataSources.remoteBackend.requestDtos.SignInRequestDto
import com.fstengineering.leasilycrm.core.data.dataSources.remoteBackend.requestDtos.UpdateUserRequestDto
import com.fstengineering.leasilycrm.core.domain.models.User
import com.fstengineering.leasilycrm.core.domain.repositories.UserRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

typealias WatchUserFlowCollector = FlowCollector<AppResult<User, DataSourceError>>

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
                isUserEditSynced = true,
            )
        }
            .onError { return AppResult.Error(error = it) }

        plainPreferencesStorage.savePreferences { prefs ->
            prefs.copy(didUserExplicitlySignOut = false)
        }
            .onError { return AppResult.Error(error = it) }

        return AppResult.Success(data = Unit)
    }

    override fun watchUser(): Flow<AppResult<User, DataSourceError>> = flow {
        emitCachedUser()
        fetchAndCacheRemoteUser()
        watchCachedUserForUpdates()
    }

    private suspend fun WatchUserFlowCollector.emitCachedUser() {
        encryptedPreferencesStorage.readPreferences()
            .onError { emit(AppResult.Error(error = it)) }
            .onSuccess { prefsFlow ->
                val prefs = prefsFlow.first()

                if (prefs.sessionCookie == null) {
                    emit(AppResult.Error(error = DataSourceError.RemoteBackendError.Unauthorized))
                    return
                }

                val cachedUser = prefs.toUser()
                emit(AppResult.Success(data = cachedUser))
            }
    }

    private suspend fun WatchUserFlowCollector.fetchAndCacheRemoteUser() {
        // If the cached logged user isn't synced, don't fetch user
        encryptedPreferencesStorage.readPreferences()
            .onError { emit(AppResult.Error(error = it)) }
            .onSuccess { prefsFlow ->
                val isUserEditSynced = prefsFlow.first().isUserEditSynced
                if (!isUserEditSynced) return
            }

        // Fetch user from remote backend API
        val fetchedUser = userApi.getUser()
            .fold(
                onError = {
                    emit(AppResult.Error(error = it))
                    return
                },
                onSuccess = { responseDto -> responseDto.toUser() }
            )

        // Cache the fetched user
        encryptedPreferencesStorage.savePreferences { prefs ->
            prefs.copy(
                name = fetchedUser.name,
                creciNumber = fetchedUser.creciNumber,
                profilePictureUrl = fetchedUser.profilePictureUrl,
            )
        }
            .onError { emit(AppResult.Error(error = it)) }

        emit(AppResult.Success(data = fetchedUser))
    }

    private suspend fun WatchUserFlowCollector.watchCachedUserForUpdates() {
        encryptedPreferencesStorage.readPreferences()
            .onError { emit(AppResult.Error(error = it)) }
            .onSuccess { prefsFlow ->
                emitAll(
                    prefsFlow
                        .map { AppResult.Success<User, DataSourceError>(data = it.toUser()) }
                        .distinctUntilChanged()
                )
            }
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
                if (remoteBackendError.isRedirectError()) {
                    deleteCachedUser()
                        .onError { return AppResult.Error(error = it) }
                }

                encryptedPreferencesStorage.savePreferences { prefs ->
                    prefs.copy(isUserEditSynced = false)
                }
                    .onError { return AppResult.Error(error = it) }

                return AppResult.Error(error = remoteBackendError)
            }

        encryptedPreferencesStorage.savePreferences { prefs ->
            prefs.copy(isUserEditSynced = true)
        }
            .onError { return AppResult.Error(error = it) }

        return AppResult.Success(data = Unit)
    }

    private suspend fun deleteCachedUser(): AppResult<Unit, DataSourceError> {
        return encryptedPreferencesStorage.savePreferences { prefs ->
            prefs.copy(
                sessionCookie = null,
                name = null,
                creciNumber = null,
                profilePictureUrl = null,
            )
        }
    }

    override suspend fun retrySendUserUpdate(): AppResult<Unit, DataSourceError> {
        val prefs = encryptedPreferencesStorage.readPreferences()
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { prefsFlow -> prefsFlow.first() },
            )

        // Check if should retry syncing local user data
        if (prefs.sessionCookie == null || prefs.isUserEditSynced) {
            return AppResult.Success(data = Unit)
        }

        val cachedUser = prefs.toUser()

        val updateUserRequestDto = UpdateUserRequestDto(
            name = cachedUser.name!!,
            creciNumber = cachedUser.creciNumber,
        )

        userApi.updateUser(updateUserRequestDto = updateUserRequestDto)
            .onError { remoteBackendError ->
                if (remoteBackendError.isRedirectError()) {
                    deleteCachedUser()
                        .onError { return AppResult.Error(error = it) }
                }

                return AppResult.Error(error = remoteBackendError)
            }

        encryptedPreferencesStorage.savePreferences {
            it.copy(isUserEditSynced = true)
        }
            .onError { return AppResult.Error(error = it) }

        return AppResult.Success(data = Unit)
    }

    override suspend fun deleteUser(): AppResult<Unit, DataSourceError> {
        userApi.deleteUser()
            .onError { remoteBackendError ->
                if (remoteBackendError.isRedirectError()) {
                    deleteCachedUser()
                        .onError { return AppResult.Error(error = it) }
                }

                return AppResult.Error(error = remoteBackendError)
            }

        deleteCachedUser()
            .onError { return AppResult.Error(error = it) }

        return AppResult.Success(data = Unit)
    }

    override suspend fun signOutUser(): AppResult<Unit, DataSourceError> {
        userApi.signOutUser()
            .onError { remoteBackendError ->
                if (remoteBackendError.isRedirectError()) {
                    deleteCachedUser()
                        .onError { return AppResult.Error(error = it) }
                }

                return AppResult.Error(error = remoteBackendError)
            }

        deleteCachedUser()
            .onError { return AppResult.Error(error = it) }

        return AppResult.Success(data = Unit)
    }
}
