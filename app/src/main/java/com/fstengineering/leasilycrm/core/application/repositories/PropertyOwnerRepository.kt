package com.fstengineering.leasilycrm.core.application.repositories

import com.fstengineering.leasilycrm.core.domain.entities.PropertyOwner
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

interface PropertyOwnerRepository {
    suspend fun insertOrUpdate(propertyOwner: PropertyOwner): AppResult<Unit, DataError>

    suspend fun delete(id: Long): AppResult<Unit, DataError>

    suspend fun getById(propertyOwnerId: Long): AppResult<PropertyOwner, DataError>

    fun watchById(propertyOwnerId: Long): Flow<AppResult<PropertyOwner, DataError>>

    fun watchAll(): Flow<AppResult<ImmutableList<PropertyOwner>, DataError>>

    suspend fun isTherePropertyOwnerWithName(
        propertyOwnerName: String,
        currentStoredPropertyOwnerName: String?,
    ): AppResult<Boolean, DataError>
}
