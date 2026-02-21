package com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.AssistanceCompound
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.infra.utils.InfraError

interface AssistanceDatabaseDataSource {
    suspend fun insertOrUpdate(assistance: AssistanceEntity): Long

    suspend fun delete(id: Long)

    suspend fun getById(id: Long): AppResult<AssistanceCompound, InfraError>

    suspend fun getAllNeighborhoodNames(): AppResult<List<String>, InfraError>
}
