package com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.PropertyCompound
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.infra.utils.InfraError
import kotlinx.coroutines.flow.Flow

interface PropertyDatabaseDataSource {
    suspend fun insertOrUpdate(property: PropertyEntity): Long

    suspend fun delete(id: Long)

    suspend fun getById(id: Long): AppResult<PropertyCompound, InfraError>

    suspend fun getAll(): AppResult<List<PropertyCompound>, InfraError>

    suspend fun getAllMatching(matching: String?): AppResult<List<PropertyCompound>, InfraError>

    suspend fun getAllNeighborHoodNames(): AppResult<List<String>, InfraError>

    fun watchById(id: Long): AppResult<Flow<PropertyCompound?>, InfraError>

    fun watchAll(): AppResult<Flow<List<PropertyCompound>>, InfraError>
}
