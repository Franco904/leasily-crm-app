package com.fstengineering.leasilycrm.core.application.stores

import com.fstengineering.leasilycrm.core.application.models.SettingsSharedReadModel
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import kotlinx.coroutines.flow.Flow

interface SettingsSharedStore {
    suspend fun setIsTodayCheckInCheckOutNotificationEnabled(
        isEnabled: Boolean,
    )

    fun watch(): Flow<SettingsSharedReadModel>
}