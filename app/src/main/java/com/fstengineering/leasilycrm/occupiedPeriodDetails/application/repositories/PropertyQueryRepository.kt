package com.fstengineering.leasilycrm.occupiedPeriodDetails.application.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.occupiedPeriodDetails.application.models.dtos.PropertyReadModel

interface PropertyQueryRepository {
    suspend fun getById(propertyId: Long): AppResult<PropertyReadModel, ApplicationError>
}
