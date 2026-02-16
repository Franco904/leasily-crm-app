package com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.PropertyCompound
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import kotlinx.coroutines.flow.Flow

interface PropertyDatabaseDataSource {
    suspend fun insertOrUpdate(property: PropertyEntity): Long

    suspend fun delete(id: Long)

    suspend fun getById(id: Long): AppResult<PropertyCompound, DataError>

    suspend fun getAll(): AppResult<List<PropertyCompound>, DataError>

    suspend fun getAllMatching(matching: String?): AppResult<List<PropertyCompound>, DataError>

    suspend fun getAllNeighborHoodNames(): AppResult<List<String>, DataError>

    fun watchById(id: Long): AppResult<Flow<PropertyCompound?>, DataError>

    fun watchAll(): AppResult<Flow<List<PropertyCompound>>, DataError>
}
