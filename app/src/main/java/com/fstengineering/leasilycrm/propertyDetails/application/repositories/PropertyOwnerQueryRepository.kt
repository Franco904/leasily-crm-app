package com.fstengineering.leasilycrm.propertyDetails.application.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.propertyRegistration.application.models.dtos.PropertyOwnerReadModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

interface PropertyOwnerQueryRepository {
    suspend fun getNameById(propertyOwnerId: Long): AppResult<String, ApplicationError>
}
