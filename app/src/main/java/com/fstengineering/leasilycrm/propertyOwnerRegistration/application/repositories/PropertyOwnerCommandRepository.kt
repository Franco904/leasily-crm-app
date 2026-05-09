package com.fstengineering.leasilycrm.propertyOwnerRegistration.application.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.propertyOwnerRegistration.domain.entities.PropertyOwner

interface PropertyOwnerCommandRepository {
    suspend fun insertOrUpdate(propertyOwner: PropertyOwner): AppResult<Unit, ApplicationError>
}
