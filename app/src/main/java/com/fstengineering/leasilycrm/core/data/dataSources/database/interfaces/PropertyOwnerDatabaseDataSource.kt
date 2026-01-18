package com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyOwnerEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.compounds.PropertyOwnerCompound
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import kotlinx.coroutines.flow.Flow

interface PropertyOwnerDatabaseDataSource {
    suspend fun insertOrUpdate(propertyOwner: PropertyOwnerEntity): AppResult<Long, DataError>

    suspend fun delete(id: Long): AppResult<Unit, DataError>

    suspend fun getById(id: Long): AppResult<PropertyOwnerCompound, DataError>

    fun watchById(id: Long): AppResult<Flow<PropertyOwnerCompound>, DataError>

    fun watchAll(userId: Long): AppResult<Flow<List<PropertyOwnerCompound>>, DataError>

    suspend fun countPropertyOwnersWithName(
        userId: Long,
        propertyOwnerName: String,
        currentStoredPropertyOwnerName: String?,
    ): AppResult<Int, DataError>
}
