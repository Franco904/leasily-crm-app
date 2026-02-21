package com.fstengineering.leasilycrm.core.application.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.entities.PropertyOwner
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

interface PropertyOwnerRepository {
    suspend fun insertOrUpdate(propertyOwner: PropertyOwner): AppResult<Unit, ApplicationError>

    suspend fun delete(id: Long): AppResult<Unit, ApplicationError>

    suspend fun getById(propertyOwnerId: Long): AppResult<PropertyOwner, ApplicationError>

    fun watchById(propertyOwnerId: Long): Flow<AppResult<PropertyOwner, ApplicationError>>

    fun watchAll(): Flow<AppResult<ImmutableList<PropertyOwner>, ApplicationError>>

    suspend fun isTherePropertyOwnerWithName(
        propertyOwnerName: String,
        currentStoredPropertyOwnerName: String?,
    ): AppResult<Boolean, ApplicationError>
}
