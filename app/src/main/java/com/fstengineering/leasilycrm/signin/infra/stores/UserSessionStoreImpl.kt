package com.fstengineering.leasilycrm.signin.infra.stores

import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.encrypted.EncryptedPreferences
import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.interfaces.PreferencesDataSource
import com.fstengineering.leasilycrm.core.infra.models.SyncState
import com.fstengineering.leasilycrm.signin.application.models.dtos.SignInResultWriteModel
import com.fstengineering.leasilycrm.signin.application.stores.UserSessionStore

class UserSessionStoreImpl(
    private val encryptedPreferencesDataSource: PreferencesDataSource<EncryptedPreferences>,
) : UserSessionStore {
    override suspend fun storeSignedInUser(signInResult: SignInResultWriteModel) {
        encryptedPreferencesDataSource.savePreferences { prefs ->
            prefs.copy(
                remoteId = signInResult.remoteId,
                name = signInResult.name,
                creciNumber = signInResult.creci,
                profilePictureUrl = signInResult.profilePictureUrl,
                userContractVersion = signInResult.contractVersion,
                userSyncStateId = SyncState.Synced.id,
            )
        }
    }
}
