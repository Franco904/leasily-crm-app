package com.fstengineering.leasilycrm.profile.application.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.profile.application.models.dtos.UserReadModel
import kotlinx.coroutines.flow.Flow

interface UserQueryRepository {
    fun watch(): Flow<AppResult<UserReadModel, ApplicationError>>
}
