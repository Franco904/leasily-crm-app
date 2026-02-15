package com.fstengineering.leasilycrm.core.data.repositories

import com.fstengineering.leasilycrm.core.data.models.SyncState
import com.fstengineering.leasilycrm.core.infra.adapters.interfaces.ConnectivityAdapter
import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.encrypted.EncryptedPreferences
import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.interfaces.PreferencesDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.remote.interfaces.UserRemoteDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.remote.requestDtos.user.SignInRequestDto
import com.fstengineering.leasilycrm.core.domain.models.User
import com.fstengineering.leasilycrm.core.domain.repositories.AuthRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import kotlinx.coroutines.flow.firstOrNull

class AuthRepositoryImpl(
    private val connectivityAdapter: ConnectivityAdapter,
    private val remoteDataSource: UserRemoteDataSource,
    private val encryptedPreferencesDataSource: PreferencesDataSource<EncryptedPreferences>,
) : AuthRepository {
    override suspend fun isSessionAlive(): AppResult<Boolean, DataError> {
        return encryptedPreferencesDataSource.readPreferences()
            .fold(
                onError = { error -> AppResult.Error(error = error) },
                onSuccess = { prefsFlow ->
                    val isSessionAlive = prefsFlow.firstOrNull()?.sessionCookie != null

                    AppResult.Success(data = isSessionAlive)
                }
            )
    }

    override suspend fun signIn(idToken: String): AppResult<User, DataError> {
        val signInRequestDto = SignInRequestDto(idToken = idToken)
        val signInResponseDto = remoteDataSource.signInUser(signInRequestDto = signInRequestDto)
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { responseDto -> responseDto }
            )

        val user = User(
            remoteId = signInResponseDto.id,
            name = signInResponseDto.name,
            creciNumber = signInResponseDto.creciNumber,
            profilePictureUrl = signInResponseDto.profilePictureUrl,
            userSyncStateId = SyncState.Synced.id,
        )

        return AppResult.Success(data = user)
    }

    override suspend fun signOut(): AppResult<Unit, DataError> {
        if (!connectivityAdapter.isDeviceConnectedToInternet(canUseMobile = true)) {
            return AppResult.Error(error = DataError.NoInternetError)
        }

        remoteDataSource.signOutUser()
            .onError { e ->
                if (e != DataError.UnauthorizedError) return AppResult.Error(error = e)
            }

        encryptedPreferencesDataSource.savePreferences { prefs ->
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

    override fun checkDeviceIsConnectedToInternet(): AppResult<Unit, DataError> {
        if (!connectivityAdapter.isDeviceConnectedToInternet(canUseMobile = true)) {
            return AppResult.Error(error = DataError.NoInternetError)
        }

        return AppResult.Success(data = Unit)
    }
}
