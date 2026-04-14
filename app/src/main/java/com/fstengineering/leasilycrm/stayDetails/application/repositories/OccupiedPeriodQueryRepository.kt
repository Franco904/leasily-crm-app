package com.fstengineering.leasilycrm.stayDetails.application.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.stayDetails.application.models.dtos.OccupiedPeriodRawReadModel
import kotlinx.coroutines.flow.Flow

interface OccupiedPeriodQueryRepository {
    fun watchById(stayId: Long): Flow<AppResult<OccupiedPeriodRawReadModel?, ApplicationError>>

    fun watchMustNotShowCheckOutDialogAnymore(): Flow<AppResult<Boolean, ApplicationError>>
}
