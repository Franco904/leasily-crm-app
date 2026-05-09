package com.fstengineering.leasilycrm.core.application.stores

import com.fstengineering.leasilycrm.core.application.models.SettingsSharedReadModel
import kotlinx.coroutines.flow.Flow

interface SettingsSharedStore {
    suspend fun setIsTodayCheckInCheckOutNotificationEnabled(
        isEnabled: Boolean,
    )

    fun watch(): Flow<SettingsSharedReadModel>
}