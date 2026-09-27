package com.fstengineering.leasilycrm.occupiedPeriodDetails.application.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.occupiedPeriodDetails.application.models.dtos.OccupiedPeriodRawReadModel
import kotlinx.coroutines.flow.Flow

interface OccupiedPeriodQueryRepository {
    fun watchById(stayId: Long): Flow<AppResult<OccupiedPeriodRawReadModel?, ApplicationError>>

    fun watchMustNotShowCheckOutDialogAnymore(): Flow<AppResult<Boolean, ApplicationError>>
}
