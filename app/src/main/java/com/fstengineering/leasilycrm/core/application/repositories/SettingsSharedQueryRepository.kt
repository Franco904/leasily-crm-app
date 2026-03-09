package com.fstengineering.leasilycrm.core.application.repositories

import com.fstengineering.leasilycrm.core.application.models.SettingsSharedReadModel
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import kotlinx.coroutines.flow.Flow

interface SettingsSharedQueryRepository {
    fun watch(): Flow<AppResult<SettingsSharedReadModel, ApplicationError>>
}
