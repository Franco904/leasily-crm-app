package com.fstengineering.leasilycrm.settings.application.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.settings.application.models.SettingsReadModel
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun watch(): Flow<AppResult<SettingsReadModel, ApplicationError>>

    suspend fun setIsTodayCheckInCheckOutNotificationEnabled(
        isEnabled: Boolean,
    ): AppResult<Unit, ApplicationError>
}
