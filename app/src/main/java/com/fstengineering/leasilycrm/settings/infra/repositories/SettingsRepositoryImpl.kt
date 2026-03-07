package com.fstengineering.leasilycrm.settings.infra.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.interfaces.PreferencesDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.plain.PlainPreferences
import com.fstengineering.leasilycrm.settings.application.repositories.SettingsRepository
import com.fstengineering.leasilycrm.settings.application.models.SettingsReadModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class SettingsRepositoryImpl(
    private val plainPreferencesDataSource: PreferencesDataSource<PlainPreferences>,
) : SettingsRepository {
    override fun watch(): Flow<AppResult<SettingsReadModel, ApplicationError>> = flow {
        plainPreferencesDataSource.readPreferences()
            .fold(
                onError = { return@flow emit(AppResult.Error(error = it.toApplicationError())) },
                onSuccess = { prefsFlow ->
                    val settingsFlow = prefsFlow.map {
                        val settings = SettingsReadModel(
                            isTodayCheckInCheckOutNotificationEnabled = it.isTodayCheckInCheckOutNotificationEnabled == true,
                        )

                        AppResult.Success<SettingsReadModel, ApplicationError>(data = settings)
                    }

                    emitAll(settingsFlow)
                }
            )
    }

    override suspend fun setIsTodayCheckInCheckOutNotificationEnabled(
        isEnabled: Boolean,
    ): AppResult<Unit, ApplicationError> {
        plainPreferencesDataSource.savePreferences { prefs ->
            prefs.copy(isTodayCheckInCheckOutNotificationEnabled = isEnabled)
        }
            .onError { return AppResult.Error(error = it.toApplicationError()) }

        return AppResult.Success(data = Unit)
    }
}