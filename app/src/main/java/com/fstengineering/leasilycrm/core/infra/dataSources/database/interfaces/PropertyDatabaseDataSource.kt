package com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.PropertyCompound
import com.fstengineering.leasilycrm.core.infra.utils.InfraError
import kotlinx.coroutines.flow.Flow

interface PropertyDatabaseDataSource {
    suspend fun insertOrUpdate(property: PropertyEntity): Long

    suspend fun delete(id: Long)

    suspend fun getById(id: Long): AppResult<PropertyCompound, InfraError>

    suspend fun getAll(matching: String? = null): AppResult<List<PropertyCompound>, InfraError>

    suspend fun getAllNeighborhoodNames(): AppResult<List<String>, InfraError>

    suspend fun getAllSentInAssistance(assistanceId: Long): AppResult<List<PropertyCompound>, InfraError>

    fun watchById(id: Long): Flow<AppResult<PropertyCompound, InfraError>>

    fun watchAll(): AppResult<Flow<List<PropertyCompound>>, InfraError>
}
