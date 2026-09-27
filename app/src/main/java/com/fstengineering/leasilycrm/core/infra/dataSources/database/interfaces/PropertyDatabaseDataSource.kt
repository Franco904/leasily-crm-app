package com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.PropertyCompound
import kotlinx.coroutines.flow.Flow

interface PropertyDatabaseDataSource {
    suspend fun insertOrUpdate(property: PropertyEntity): Long

    suspend fun delete(id: Long)

    suspend fun getById(id: Long): AppResult<PropertyCompound, ApplicationError>

    suspend fun getAll(matching: String? = null): AppResult<List<PropertyCompound>, ApplicationError>

    suspend fun getAllNeighborhoodNames(): AppResult<List<String>, ApplicationError>

    suspend fun getAllSentInAssistance(assistanceId: Long): AppResult<List<PropertyCompound>, ApplicationError>

    fun watchById(id: Long): Flow<AppResult<PropertyCompound, ApplicationError>>

    fun watchAll(): AppResult<Flow<List<PropertyCompound>>, ApplicationError>
}
