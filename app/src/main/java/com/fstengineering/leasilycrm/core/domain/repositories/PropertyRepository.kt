package com.fstengineering.leasilycrm.core.domain.repositories

import com.fstengineering.leasilycrm.core.domain.models.Property
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

interface PropertyRepository {
    suspend fun insertOrUpdate(property: Property): AppResult<Int, DataSourceError>

    suspend fun delete(id: Int): AppResult<Unit, DataSourceError>

    suspend fun getById(propertyId: Int): AppResult<Property, DataSourceError>

    fun watchById(propertyId: Int): Flow<AppResult<Property, DataSourceError>>

    fun watchAll(): Flow<AppResult<ImmutableList<Property>, DataSourceError>>
}
