package com.fstengineering.leasilycrm.propertyDashboard.application.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.propertyDashboard.application.models.dtos.PropertyRawReadModel
import kotlinx.coroutines.flow.Flow

interface PropertyQueryRepository {
    fun watchById(propertyId: Long): Flow<AppResult<PropertyRawReadModel, ApplicationError>>
}
