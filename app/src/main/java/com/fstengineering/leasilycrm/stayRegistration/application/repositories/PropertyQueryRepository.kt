package com.fstengineering.leasilycrm.stayRegistration.application.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.stayRegistration.application.models.dtos.PropertyReadModel
import kotlinx.collections.immutable.ImmutableList

interface PropertyQueryRepository {
    suspend fun getById(propertyId: Long): AppResult<PropertyReadModel, ApplicationError>

    suspend fun getAll(
        matching: String?,
    ): AppResult<ImmutableList<PropertyReadModel>, ApplicationError>
}
