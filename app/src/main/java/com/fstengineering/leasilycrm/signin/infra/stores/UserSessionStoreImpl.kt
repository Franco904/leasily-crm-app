package com.fstengineering.leasilycrm.signin.infra.stores

import com.fstengineering.leasilycrm.core.infra.localStorage.encrypted.EncryptedPreferences
import com.fstengineering.leasilycrm.core.infra.localStorage.interfaces.LocalStorage
import com.fstengineering.leasilycrm.core.infra.projections.SyncState
import com.fstengineering.leasilycrm.signin.application.models.dtos.SignInResultWriteModel
import com.fstengineering.leasilycrm.signin.application.stores.UserSessionStore

class UserSessionStoreImpl(
    private val encryptedLocalStorage: LocalStorage<EncryptedPreferences>,
) : UserSessionStore {
    override suspend fun storeSignedInUser(signInResult: SignInResultWriteModel) {
        encryptedLocalStorage.savePreferences { prefs ->
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
