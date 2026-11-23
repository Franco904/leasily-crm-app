package com.fstengineering.leasilycrm.home.infra.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.infra.localStorage.encrypted.EncryptedPreferences
import com.fstengineering.leasilycrm.core.infra.localStorage.interfaces.LocalStorage
import com.fstengineering.leasilycrm.home.application.repositories.UserQueryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class UserQueryRepositoryImpl(
    private val encryptedLocalStorage: LocalStorage<EncryptedPreferences>,
) : UserQueryRepository {
    override fun watchUserProfilePictureUrl(): Flow<AppResult<String?>> = flow {
        encryptedLocalStorage.readPreferences()
            .onError { emit(AppResult.Error(error = it)) }
            .onSuccess { prefsFlow ->
                emitAll(
                    prefsFlow
                        .map { AppResult.Success<String?>(data = it.profilePictureUrl) }
                        .distinctUntilChanged()
                )
            }
    }
}
