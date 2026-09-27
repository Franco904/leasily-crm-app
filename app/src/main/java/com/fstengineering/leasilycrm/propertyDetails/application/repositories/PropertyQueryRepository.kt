package com.fstengineering.leasilycrm.propertyDetails.application.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.propertyDetails.application.models.dtos.PropertyReadModel
import kotlinx.coroutines.flow.Flow

interface PropertyQueryRepository {
    fun watchById(propertyId: Long): Flow<AppResult<PropertyReadModel, ApplicationError>>
}
