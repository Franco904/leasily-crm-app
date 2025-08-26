package com.fstengineering.leasilycrm.core.domain.repositories

import com.fstengineering.leasilycrm.core.domain.models.PropertyOwner
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

interface PropertyOwnerRepository {
    suspend fun insertOrUpdate(
        propertyOwner: PropertyOwner,
    ): AppResult<Unit, DataSourceError>

    suspend fun delete(id: Int): AppResult<Unit, DataSourceError>

    suspend fun watchAll(): Flow<AppResult<ImmutableList<PropertyOwner>, DataSourceError>>
}
