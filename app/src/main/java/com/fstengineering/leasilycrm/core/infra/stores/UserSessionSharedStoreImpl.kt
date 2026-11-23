package com.fstengineering.leasilycrm.core.infra.stores

import com.fstengineering.leasilycrm.core.application.stores.UserSessionSharedStore
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.infra.localStorage.encrypted.EncryptedPreferences
import com.fstengineering.leasilycrm.core.infra.localStorage.interfaces.LocalStorage
import kotlinx.coroutines.flow.firstOrNull

class UserSessionSharedStoreImpl(
    private val encryptedLocalStorage: LocalStorage<EncryptedPreferences>,
) : UserSessionSharedStore {
    override suspend fun isSessionAlive(): Boolean {
        return encryptedLocalStorage.readPreferences()
            .fold(
                onError = { false },
                onSuccess = { prefsFlow ->
                    val isSessionAlive = prefsFlow.firstOrNull()?.sessionCookie != null
                    isSessionAlive
                }
            )
    }

    override suspend fun clearSessionState() {
        encryptedLocalStorage.savePreferences { prefs ->
            prefs.copy(
                sessionCookie = null,
                name = null,
                creciNumber = null,
                profilePictureUrl = null,
            )
        }
    }
}
