package com.fstengineering.leasilycrm.core.infra.repositories

import com.fstengineering.leasilycrm.core.application.repositories.SettingsRepository
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.valueObjects.Settings
import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.interfaces.PreferencesDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.plain.PlainPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class SettingsRepositoryImpl(
    private val plainPreferencesDataSource: PreferencesDataSource<PlainPreferences>,
) : SettingsRepository {
    override fun watchSettings(): Flow<AppResult<Settings, ApplicationError>> = flow {
        plainPreferencesDataSource.readPreferences()
            .fold(
                onError = { return@flow emit(AppResult.Error(error = it.toApplication())) },
                onSuccess = { prefsFlow ->
                    val settingsFlow = prefsFlow.map {
                        val settings = Settings(
                            isTodayCheckInCheckOutNotificationEnabled = it.isTodayCheckInCheckOutNotificationEnabled == true,
                        )

                        AppResult.Success<Settings, ApplicationError>(data = settings)
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
            .onError { return AppResult.Error(error = it.toApplication()) }

        return AppResult.Success(data = Unit)
    }
}
