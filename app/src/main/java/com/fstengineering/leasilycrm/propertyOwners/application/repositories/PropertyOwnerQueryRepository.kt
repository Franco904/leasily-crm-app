package com.fstengineering.leasilycrm.propertyOwners.application.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.propertyOwners.application.models.dtos.PropertyOwnerReadModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

interface PropertyOwnerQueryRepository {
    fun watchAll(): Flow<AppResult<ImmutableList<PropertyOwnerReadModel>, ApplicationError>>
}
