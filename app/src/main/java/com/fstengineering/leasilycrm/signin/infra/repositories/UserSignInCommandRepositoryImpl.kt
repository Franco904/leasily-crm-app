package com.fstengineering.leasilycrm.signin.infra.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.encrypted.EncryptedPreferences
import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.interfaces.PreferencesDataSource
import com.fstengineering.leasilycrm.core.infra.models.SyncState
import com.fstengineering.leasilycrm.signin.application.repositories.UserSignInCommandRepository
import com.fstengineering.leasilycrm.signin.domain.entities.User

class UserSignInCommandRepositoryImpl(
    private val encryptedPreferencesDataSource: PreferencesDataSource<EncryptedPreferences>,
) : UserSignInCommandRepository {
    override suspend fun store(user: User): AppResult<Unit, ApplicationError> {
        encryptedPreferencesDataSource.savePreferences { prefs ->
            prefs.copy(
                remoteId = user.remoteId,
                name = user.name,
                creciNumber = user.creci,
                profilePictureUrl = user.profilePictureUrl,
                userContractVersion = user.contractVersion,
                userSyncStateId = SyncState.Synced.id,
            )
        }
            .onError { return AppResult.Error(error = it.toApplicationError()) }

        return AppResult.Success(data = Unit)
    }
}
