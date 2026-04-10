package com.fstengineering.leasilycrm.core.infra.dataSources.database

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.LeadTenantDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.LeadTenantEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.TenantDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.database.views.LeadTenantReadView
import com.fstengineering.leasilycrm.core.infra.utils.InfraError
import kotlinx.coroutines.flow.Flow

class TenantDatabaseDataSourceImpl(
    private val dao: LeadTenantDao,
) : TenantDatabaseDataSource {
    override suspend fun insertOrUpdate(tenant: LeadTenantEntity): Long =
        dao.insertOrUpdate(tenant)

    override suspend fun delete(id: Long) =
        dao.delete(id)

    override suspend fun getById(id: Long): AppResult<LeadTenantReadView, InfraError> {
        return onDatabaseSafe { dao.getById(id) }
    }

    override suspend fun getNameById(id: Long): AppResult<String, InfraError> {
        return onDatabaseSafe { dao.getNameById(id) }
    }

    override suspend fun getAllLeads(
        matching: String?,
        order: Int,
    ): AppResult<List<LeadTenantReadView>, InfraError> {
        return onDatabaseSafe { dao.getAllLeads(matching, order) }
    }

    override suspend fun getAllTenants(
        matching: String?,
        order: Int,
    ): AppResult<List<LeadTenantReadView>, InfraError> {
        return onDatabaseSafe { dao.getAllTenants(matching, order) }
    }

    override suspend fun getAllWithoutUpcomingStays(
        matching: String?,
        currentTenantId: Long?,
    ): AppResult<List<LeadTenantReadView>, InfraError> {
        return onDatabaseSafe {
            dao.getAllWithoutUpcomingStays(
                matching = matching,
                currentTenantId = currentTenantId,
            )
        }
    }

    override suspend fun getAllTagNames(): AppResult<List<String>, InfraError> {
        return onDatabaseSafe { dao.getAllTagNames() }
    }

    override suspend fun getAllCityNames(): AppResult<List<String>, InfraError> {
        return onDatabaseSafe { dao.getAllCityNames() }
    }

    override suspend fun getAllContactOrigins(): AppResult<List<String>, InfraError> {
        return onDatabaseSafe { dao.getAllContactOrigins() }
    }

    override fun watchById(id: Long): AppResult<Flow<LeadTenantReadView?>, InfraError> {
        return onDatabaseSafe { dao.watchById(id) }
    }

    override fun watchAll(
        tenantStatusIdFilter: Int?,
        matching: String?,
        orderTypeId: Int?,
    ): AppResult<Flow<List<LeadTenantReadView>>, InfraError> {
        return onDatabaseSafe {
            dao.watchAll(
                tenantStatusIdFilter = tenantStatusIdFilter,
                matching = matching,
                orderTypeId = orderTypeId,
            )
        }
    }
}
