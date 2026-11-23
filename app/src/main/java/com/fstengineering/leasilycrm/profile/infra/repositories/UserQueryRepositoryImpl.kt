package com.fstengineering.leasilycrm.profile.infra.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.infra.localStorage.encrypted.EncryptedPreferences
import com.fstengineering.leasilycrm.core.infra.localStorage.interfaces.LocalStorage
import com.fstengineering.leasilycrm.profile.application.models.dtos.UserReadModel
import com.fstengineering.leasilycrm.profile.application.repositories.UserQueryRepository
import com.fstengineering.leasilycrm.profile.infra.mappers.toReadModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapNotNull

class UserQueryRepositoryImpl(
    private val encryptedLocalStorage: LocalStorage<EncryptedPreferences>,
) : UserQueryRepository {
    override fun watch(): Flow<AppResult<UserReadModel>> = flow {
        encryptedLocalStorage.readPreferences()
            .onError { emit(AppResult.Error(error = it)) }
            .onSuccess { prefsFlow ->
                emitAll(
                    prefsFlow
                        .mapNotNull {
                            val readModel = it.toReadModel() ?: return@mapNotNull null
                            AppResult.Success<UserReadModel>(data = readModel)
                        }
                        .distinctUntilChanged()
                )
            }
    }
}
