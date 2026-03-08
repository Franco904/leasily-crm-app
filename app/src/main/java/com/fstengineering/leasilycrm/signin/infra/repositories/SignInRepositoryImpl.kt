package com.fstengineering.leasilycrm.signin.infra.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.encrypted.EncryptedPreferences
import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.interfaces.PreferencesDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.remote.interfaces.UserRemoteDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.remote.requestDtos.user.SignInRequestDto
import com.fstengineering.leasilycrm.core.infra.models.SyncState
import com.fstengineering.leasilycrm.signin.application.repositories.SignInRepository
import com.fstengineering.leasilycrm.signin.infra.mappers.toWriteModel

class SignInRepositoryImpl(
    private val remoteDataSource: UserRemoteDataSource,
    private val encryptedPreferencesDataSource: PreferencesDataSource<EncryptedPreferences>,
) : SignInRepository {
    override suspend fun signIn(idToken: String): AppResult<Unit, ApplicationError> {
        val signInRequestDto = SignInRequestDto(idToken = idToken)
        val signInResponseDto = remoteDataSource.signInUser(signInRequestDto = signInRequestDto)
            .fold(
                onError = { return AppResult.Error(error = it.toApplicationError()) },
                onSuccess = { responseDto -> responseDto }
            )

        val writeModel = signInResponseDto.toWriteModel()

        return encryptedPreferencesDataSource.savePreferences { prefs ->
            prefs.copy(
                remoteId = writeModel.remoteId,
                name = writeModel.name,
                creciNumber = writeModel.creci,
                profilePictureUrl = writeModel.profilePictureUrl,
                userContractVersion = writeModel.contractVersion,
                userSyncStateId = SyncState.Synced.id,
            )
        }
            .fold(
                onError = { AppResult.Error(error = it.toApplicationError()) },
                onSuccess = { AppResult.Success(data = Unit) }
            )
    }
}
