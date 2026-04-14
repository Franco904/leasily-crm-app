package com.fstengineering.leasilycrm.stayDetails.application.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.stayDetails.application.models.dtos.PropertyReadModel

interface PropertyQueryRepository {
    suspend fun getById(propertyId: Long): AppResult<PropertyReadModel, ApplicationError>
}
