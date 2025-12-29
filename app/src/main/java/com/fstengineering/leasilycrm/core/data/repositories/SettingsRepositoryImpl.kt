package com.fstengineering.leasilycrm.core.data.repositories

import com.fstengineering.leasilycrm.core.data.dataSources.preferences.interfaces.PreferencesStorage
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.plain.PlainPreferences
import com.fstengineering.leasilycrm.core.domain.models.Settings
import com.fstengineering.leasilycrm.core.domain.repositories.SettingsRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class SettingsRepositoryImpl(
    private val plainPreferencesStorage: PreferencesStorage<PlainPreferences>,
) : SettingsRepository {
    override fun watchSettings(): Flow<AppResult<Settings, DataError>> = flow {
        plainPreferencesStorage.readPreferences()
            .fold(
                onError = { error ->
                    emit(AppResult.Error(error = error))
                    return@flow
                },
                onSuccess = { prefsFlow ->
                    val settingsFlow = prefsFlow.map {
                        val settings = Settings(
                            isTodayCheckInCheckOutNotificationEnabled = it.isTodayCheckInCheckOutNotificationEnabled == true,
                        )

                        AppResult.Success<Settings, DataError>(data = settings)
                    }

                    emitAll(settingsFlow)
                }
            )
    }

    override suspend fun setIsTodayCheckInCheckOutNotificationEnabled(
        isEnabled: Boolean,
    ): AppResult<Unit, DataError> {
        plainPreferencesStorage.savePreferences { prefs ->
            prefs.copy(isTodayCheckInCheckOutNotificationEnabled = isEnabled)
        }
            .onError { return AppResult.Error(error = it) }

        return AppResult.Success(data = Unit)
    }
}
