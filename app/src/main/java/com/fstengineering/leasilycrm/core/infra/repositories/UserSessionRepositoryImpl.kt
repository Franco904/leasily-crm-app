package com.fstengineering.leasilycrm.core.infra.repositories

import com.fstengineering.leasilycrm.core.application.repositories.UserSessionRepository
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.infra.adapters.interfaces.ConnectivityAdapter
import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.encrypted.EncryptedPreferences
import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.interfaces.PreferencesDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.remote.interfaces.UserRemoteDataSource
import com.fstengineering.leasilycrm.core.infra.utils.InfraError
import kotlinx.coroutines.flow.firstOrNull

class UserSessionRepositoryImpl(
    private val connectivityAdapter: ConnectivityAdapter,
    private val remoteDataSource: UserRemoteDataSource,
    private val encryptedPreferencesDataSource: PreferencesDataSource<EncryptedPreferences>,
) : UserSessionRepository {
    override suspend fun isSessionAlive(): AppResult<Boolean, ApplicationError> {
        return encryptedPreferencesDataSource.readPreferences()
            .fold(
                onError = { error -> AppResult.Error(error = error.toApplicationError()) },
                onSuccess = { prefsFlow ->
                    val isSessionAlive = prefsFlow.firstOrNull()?.sessionCookie != null

                    AppResult.Success(data = isSessionAlive)
                }
            )
    }

    override suspend fun killSession(): AppResult<Unit, ApplicationError> {
        if (!connectivityAdapter.isDeviceConnectedToInternet(canUseMobile = true)) {
            return AppResult.Error(error = ApplicationError.Infra.NoInternet)
        }

        remoteDataSource.signOutUser()
            .onError { e ->
                if (e != InfraError.Unauthorized) return AppResult.Error(error = e.toApplicationError())
            }

        encryptedPreferencesDataSource.savePreferences { prefs ->
            prefs.copy(
                sessionCookie = null,
                name = null,
                creciNumber = null,
                profilePictureUrl = null,
            )
        }
            .onError { return AppResult.Error(error = it.toApplicationError()) }

        return AppResult.Success(data = Unit)
    }
}
