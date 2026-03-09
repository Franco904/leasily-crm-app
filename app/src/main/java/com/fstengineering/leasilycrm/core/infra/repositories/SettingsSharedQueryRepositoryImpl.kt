package com.fstengineering.leasilycrm.core.infra.repositories

import com.fstengineering.leasilycrm.core.application.models.SettingsSharedReadModel
import com.fstengineering.leasilycrm.core.application.repositories.SettingsSharedQueryRepository
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.interfaces.PreferencesDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.plain.PlainPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class SettingsSharedQueryRepositoryImpl(
    private val plainPreferencesDataSource: PreferencesDataSource<PlainPreferences>,
) : SettingsSharedQueryRepository {
    override fun watch(): Flow<AppResult<SettingsSharedReadModel, ApplicationError>> = flow {
        plainPreferencesDataSource.readPreferences()
            .fold(
                onError = { return@flow emit(AppResult.Error(error = it.toApplicationError())) },
                onSuccess = { prefsFlow ->
                    val settingsFlow = prefsFlow.map {
                        val settings = SettingsSharedReadModel(
                            isTodayCheckInCheckOutNotificationEnabled = it.isTodayCheckInCheckOutNotificationEnabled == true,
                        )

                        AppResult.Success<SettingsSharedReadModel, ApplicationError>(data = settings)
                    }

                    emitAll(settingsFlow)
                }
            )
    }
}
