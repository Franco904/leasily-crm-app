package com.fstengineering.leasilycrm.propertyRegistration.application.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.propertyRegistration.application.models.dtos.PropertyRawReadModel
import kotlinx.collections.immutable.ImmutableList

interface PropertyQueryRepository {
    suspend fun getById(propertyId: Long): AppResult<PropertyRawReadModel, ApplicationError>

    suspend fun getAllNeighborhoodNames(): AppResult<ImmutableList<String>, ApplicationError>
}