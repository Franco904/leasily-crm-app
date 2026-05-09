package com.fstengineering.leasilycrm.core.infra.stores

import com.fstengineering.leasilycrm.core.application.stores.UserSessionSharedStore
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.encrypted.EncryptedPreferences
import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.interfaces.PreferencesDataSource
import kotlinx.coroutines.flow.firstOrNull

class UserSessionSharedStoreImpl(
    private val encryptedPreferencesDataSource: PreferencesDataSource<EncryptedPreferences>,
) : UserSessionSharedStore {
    override suspend fun isSessionAlive(): Boolean {
        return encryptedPreferencesDataSource.readPreferences()
            .fold(
                onError = { false },
                onSuccess = { prefsFlow ->
                    val isSessionAlive = prefsFlow.firstOrNull()?.sessionCookie != null
                    isSessionAlive
                }
            )
    }

    override suspend fun clearSessionState() {
        encryptedPreferencesDataSource.savePreferences { prefs ->
            prefs.copy(
                sessionCookie = null,
                name = null,
                creciNumber = null,
                profilePictureUrl = null,
            )
        }
    }
}
