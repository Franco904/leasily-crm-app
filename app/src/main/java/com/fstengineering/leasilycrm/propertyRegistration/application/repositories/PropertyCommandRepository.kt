package com.fstengineering.leasilycrm.propertyRegistration.application.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.propertyRegistration.domain.entities.Property

interface PropertyCommandRepository {
    suspend fun insertOrUpdate(property: Property): AppResult<Unit, ApplicationError>
}