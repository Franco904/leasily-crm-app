package com.fstengineering.leasilycrm.propertyOwnerRegistration.application.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.propertyOwnerRegistration.application.models.PropertyOwnerReadModel

interface PropertyOwnerQueryRepository {
    suspend fun getById(propertyOwnerId: Long): AppResult<PropertyOwnerReadModel, ApplicationError>
}
