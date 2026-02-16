package com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyOwnerEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.PropertyOwnerCompound
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import kotlinx.coroutines.flow.Flow

interface PropertyOwnerDatabaseDataSource {
    suspend fun insertOrUpdate(propertyOwner: PropertyOwnerEntity): Long

    suspend fun delete(id: Long)

    suspend fun getById(id: Long): AppResult<PropertyOwnerCompound, DataError>

    fun watchById(id: Long): AppResult<Flow<PropertyOwnerCompound>, DataError>

    fun watchAll(): AppResult<Flow<List<PropertyOwnerCompound>>, DataError>

    suspend fun countPropertyOwnersWithName(
        propertyOwnerName: String,
        currentStoredPropertyOwnerName: String?,
    ): AppResult<Int, DataError>
}
