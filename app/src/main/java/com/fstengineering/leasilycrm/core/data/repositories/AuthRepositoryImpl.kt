package com.fstengineering.leasilycrm.core.data.repositories

import com.fstengineering.leasilycrm.core.data.adapters.interfaces.ConnectivityAdapter
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.encrypted.EncryptedPreferences
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.interfaces.PreferencesStorage
import com.fstengineering.leasilycrm.core.data.dataSources.remoteBackend.apis.interfaces.UserApi
import com.fstengineering.leasilycrm.core.data.dataSources.remoteBackend.requestDtos.user.SignInRequestDto
import com.fstengineering.leasilycrm.core.domain.models.User
import com.fstengineering.leasilycrm.core.domain.repositories.AuthRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import kotlinx.coroutines.flow.first

class AuthRepositoryImpl(
    private val connectivityAdapter: ConnectivityAdapter,
    private val api: UserApi,
    private val encryptedPreferencesStorage: PreferencesStorage<EncryptedPreferences>,
) : AuthRepository {
    override suspend fun isSessionAlive(): AppResult<Boolean, DataError> {
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

    override suspend fun signIn(idToken: String): AppResult<User, DataError> {
        val signInRequestDto = SignInRequestDto(idToken = idToken)
        val signInResponseDto = api.signInUser(signInRequestDto = signInRequestDto)
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { responseDto -> responseDto }
            )

        val user = User(
            remoteId = signInResponseDto.id,
            name = signInResponseDto.name,
            creciNumber = signInResponseDto.creciNumber,
            profilePictureUrl = signInResponseDto.profilePictureUrl,
        )

        return AppResult.Success(data = user)
    }

    override suspend fun signOut(): AppResult<Unit, DataError> {
        if (!connectivityAdapter.isDeviceConnectedToInternet(canUseMobile = true)) {
            return AppResult.Error(error = DataError.NoInternetError)
        }

        api.signOutUser()
            .onError { e ->
                if (e !is DataError.UnauthorizedError) return AppResult.Error(error = e)
            }

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

    override fun checkDeviceIsConnectedToInternet(): AppResult<Unit, DataError> {
        if (!connectivityAdapter.isDeviceConnectedToInternet(canUseMobile = true)) {
            return AppResult.Error(error = DataError.NoInternetError)
        }

        return AppResult.Success(data = Unit)
    }
}
