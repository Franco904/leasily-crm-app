package com.fstengineering.leasilycrm.core.domain.repositories

import com.fstengineering.leasilycrm.core.domain.models.PropertyOwner
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

interface PropertyOwnerRepository {
    suspend fun insertOrUpdate(
        propertyOwner: PropertyOwner,
    ): AppResult<Long, DataSourceError>

    suspend fun delete(id: Long): AppResult<Unit, DataSourceError>

    suspend fun getById(propertyOwnerId: Long): AppResult<PropertyOwner, DataSourceError>

    fun watchById(propertyOwnerId: Long): Flow<AppResult<PropertyOwner, DataSourceError>>

    fun watchAll(): Flow<AppResult<ImmutableList<PropertyOwner>, DataSourceError>>

    suspend fun isTherePropertyOwnerWithName(
        propertyOwnerName: String,
        currentStoredPropertyOwnerName: String?,
    ): AppResult<Boolean, DataSourceError>
}
