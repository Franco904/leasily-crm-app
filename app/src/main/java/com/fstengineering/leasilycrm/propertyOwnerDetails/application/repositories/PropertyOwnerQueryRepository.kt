package com.fstengineering.leasilycrm.propertyOwnerDetails.application.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.propertyOwnerDetails.application.models.PropertyOwnerReadModel
import kotlinx.coroutines.flow.Flow

interface PropertyOwnerQueryRepository {
    fun watchById(propertyOwnerId: Long): Flow<AppResult<PropertyOwnerReadModel, ApplicationError>>
}
