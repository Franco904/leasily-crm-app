package com.fstengineering.leasilycrm.core.infra.stores

import com.fstengineering.leasilycrm.core.application.models.SettingsSharedReadModel
import com.fstengineering.leasilycrm.core.application.stores.SettingsSharedStore
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.infra.localStorage.interfaces.LocalStorage
import com.fstengineering.leasilycrm.core.infra.localStorage.plain.PlainPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class SettingsSharedStoreImpl(
    private val plainLocalStorage: LocalStorage<PlainPreferences>,
) : SettingsSharedStore {
    override suspend fun setIsTodayCheckInCheckOutNotificationEnabled(
        isEnabled: Boolean,
    ) {
        plainLocalStorage.savePreferences { prefs ->
            prefs.copy(isTodayCheckInCheckOutNotificationEnabled = isEnabled)
        }
    }

    override fun watch(): Flow<SettingsSharedReadModel> = flow {
        val defaultSettings = SettingsSharedReadModel(
            isTodayCheckInCheckOutNotificationEnabled = false
        )

        plainLocalStorage.readPreferences()
            .fold(
                onError = { return@flow emit(defaultSettings) },
                onSuccess = { prefsFlow ->
                    val settingsFlow = prefsFlow.map {
                        SettingsSharedReadModel(
                            isTodayCheckInCheckOutNotificationEnabled = it.isTodayCheckInCheckOutNotificationEnabled == true,
                        )
                    }

                    emitAll(settingsFlow)
                }
            )
    }
}