package com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.compounds.PropertyCompound
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import kotlinx.coroutines.flow.Flow

interface PropertyDatabaseDataSource {
    suspend fun insertOrUpdate(property: PropertyEntity): Long

    suspend fun delete(id: Long)

    suspend fun getById(id: Long): AppResult<PropertyCompound, DataError>

    suspend fun getAll(userId: Long): AppResult<List<PropertyCompound>, DataError>

    suspend fun getAllMatching(userId: Long, matching: String?): AppResult<List<PropertyCompound>, DataError>

    suspend fun getAllNeighborHoodNames(userId: Long): AppResult<List<String>, DataError>

    fun watchById(id: Long): AppResult<Flow<PropertyCompound?>, DataError>

    fun watchAll(userId: Long): AppResult<Flow<List<PropertyCompound>>, DataError>
}
