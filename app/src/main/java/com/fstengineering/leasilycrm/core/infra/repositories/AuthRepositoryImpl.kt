package com.fstengineering.leasilycrm.core.infra.repositories

import com.fstengineering.leasilycrm.core.application.repositories.AuthRepository
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.entities.User
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.infra.adapters.interfaces.ConnectivityAdapter
import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.encrypted.EncryptedPreferences
import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.interfaces.PreferencesDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.remote.interfaces.UserRemoteDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.remote.requestDtos.user.SignInRequestDto
import com.fstengineering.leasilycrm.core.infra.utils.InfraError
import kotlinx.coroutines.flow.firstOrNull

class AuthRepositoryImpl(
    private val connectivityAdapter: ConnectivityAdapter,
    private val remoteDataSource: UserRemoteDataSource,
    private val encryptedPreferencesDataSource: PreferencesDataSource<EncryptedPreferences>,
) : AuthRepository {
    override suspend fun isSessionAlive(): AppResult<Boolean, ApplicationError> {
        return encryptedPreferencesDataSource.readPreferences()
            .fold(
                onError = { error -> AppResult.Error(error = error.toApplication()) },
                onSuccess = { prefsFlow ->
                    val isSessionAlive = prefsFlow.firstOrNull()?.sessionCookie != null

                    AppResult.Success(data = isSessionAlive)
                }
            )
    }

    override suspend fun signIn(idToken: String): AppResult<User, ApplicationError> {
        val signInRequestDto = SignInRequestDto(idToken = idToken)
        val signInResponseDto = remoteDataSource.signInUser(signInRequestDto = signInRequestDto)
            .fold(
                onError = { return AppResult.Error(error = it.toApplication()) },
                onSuccess = { responseDto -> responseDto }
            )

        val user = signInResponseDto.toDomain()

        return AppResult.Success(data = user)
    }

    override suspend fun signOut(): AppResult<Unit, ApplicationError> {
        if (!connectivityAdapter.isDeviceConnectedToInternet(canUseMobile = true)) {
            return AppResult.Error(error = ApplicationError.NoInternet)
        }

        remoteDataSource.signOutUser()
            .onError { e ->
                if (e != InfraError.Unauthorized) return AppResult.Error(error = e.toApplication())
            }

        encryptedPreferencesDataSource.savePreferences { prefs ->
            prefs.copy(
                sessionCookie = null,
                name = null,
                creciNumber = null,
                profilePictureUrl = null,
            )
        }
            .onError { return AppResult.Error(error = it.toApplication()) }

        return AppResult.Success(data = Unit)
    }

    override fun checkDeviceIsConnectedToInternet(): AppResult<Unit, ApplicationError> {
        if (!connectivityAdapter.isDeviceConnectedToInternet(canUseMobile = true)) {
            return AppResult.Error(error = ApplicationError.NoInternet)
        }

        return AppResult.Success(data = Unit)
    }
}
