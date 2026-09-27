package com.fstengineering.leasilycrm.core.infra.dataSources.database

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.AssistanceDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.AssistanceCompound
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.AssistanceDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.utils.InfraError
import com.fstengineering.leasilycrm.core.infra.utils.onDatabase

class AssistanceDatabaseDataSourceImpl(
    private val dao: AssistanceDao,
) : AssistanceDatabaseDataSource {
    override suspend fun insertOrUpdate(assistance: AssistanceEntity): Long =
        dao.insertOrUpdate(assistance)

    override suspend fun delete(id: Long) =
        dao.delete(id)

    override suspend fun getById(id: Long): AppResult<AssistanceCompound, InfraError> {
        return onDatabase { dao.getById(id) }
    }

    override suspend fun getAllByLeadTenantId(leadTenantId: Long): AppResult<List<AssistanceCompound>, InfraError> {
        return onDatabase { dao.getAllByLeadTenantId(leadTenantId) }
    }

    override suspend fun getAllNeighborhoodNames(): AppResult<List<String>, InfraError> {
        return onDatabase { dao.getAllNeighborhoodNames() }
    }

    override suspend fun finishOpenAssistanceByTenantId(tenantId: Long) =
        dao.finishOpenAssistanceByTenantId(tenantId)

    override suspend fun unfinishAssistanceByTenantId(tenantId: Long) =
        dao.unfinishAssistanceByTenantId(tenantId)

    override suspend fun getCurrentAssistanceGuestsCountByTenantId(tenantId: Long): AppResult<Int?, InfraError> {
        return onDatabase { dao.getCurrentAssistanceGuestsCountByTenantId(tenantId) }
    }
}
