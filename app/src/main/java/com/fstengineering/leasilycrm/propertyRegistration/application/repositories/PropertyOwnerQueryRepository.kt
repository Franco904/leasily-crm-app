package com.fstengineering.leasilycrm.propertyRegistration.application.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.propertyRegistration.application.models.PropertyOwnerReadModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

interface PropertyOwnerQueryRepository {
    suspend fun getById(propertyOwnerId: Long): AppResult<PropertyOwnerReadModel, ApplicationError>

    fun watchAll(): Flow<AppResult<ImmutableList<PropertyOwnerReadModel>, ApplicationError>>
}
