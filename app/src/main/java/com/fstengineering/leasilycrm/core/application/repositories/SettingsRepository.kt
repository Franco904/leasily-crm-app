package com.fstengineering.leasilycrm.core.application.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.valueObjects.Settings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun watchSettings(): Flow<AppResult<Settings, ApplicationError>>

    suspend fun setIsTodayCheckInCheckOutNotificationEnabled(
        isEnabled: Boolean,
    ): AppResult<Unit, ApplicationError>
}
