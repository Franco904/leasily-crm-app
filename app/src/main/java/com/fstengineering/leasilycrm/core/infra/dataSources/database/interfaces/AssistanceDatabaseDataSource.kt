package com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.AssistanceCompound

interface AssistanceDatabaseDataSource {
    suspend fun insertOrUpdate(assistance: AssistanceEntity): Long

    suspend fun delete(id: Long)

    suspend fun getById(id: Long): AppResult<AssistanceCompound>

    suspend fun getAllByClienteId(clienteId: Long): AppResult<List<AssistanceCompound>>

    suspend fun getAllNeighborhoodNames(): AppResult<List<String>>

    suspend fun finishOpenAssistanceByTenantId(tenantId: Long)

    suspend fun unfinishAssistanceByTenantId(tenantId: Long)

    suspend fun getCurrentAssistanceGuestsCountByTenantId(tenantId: Long): AppResult<Int?>
}
