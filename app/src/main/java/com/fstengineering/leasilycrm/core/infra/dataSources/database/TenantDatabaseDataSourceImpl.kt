package com.fstengineering.leasilycrm.core.infra.dataSources.database

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.TenantDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.TenantEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.TenantCompound
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.TenantDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.utils.InfraError
import kotlinx.coroutines.flow.Flow

class TenantDatabaseDataSourceImpl(
    private val dao: TenantDao,
) : TenantDatabaseDataSource {
    override suspend fun insertOrUpdate(tenant: TenantEntity): Long =
        dao.insertOrUpdate(tenant)

    override suspend fun delete(id: Long) =
        dao.delete(id)

    override suspend fun getById(id: Long): AppResult<TenantCompound?, InfraError> {
        return onDatabaseSafe { dao.getById(id) }
    }

    override suspend fun getNameById(id: Long): AppResult<String?, InfraError> {
        return onDatabaseSafe { dao.getNameById(id) }
    }

    override suspend fun getAllLeadsMatching(
        matching: String?,
        order: Int,
    ): AppResult<List<TenantCompound>, InfraError> {
        return onDatabaseSafe { dao.getAllLeadsMatching(matching, order) }
    }

    override suspend fun getAllTenantsMatching(
        matching: String?,
        order: Int,
    ): AppResult<List<TenantCompound>, InfraError> {
        return onDatabaseSafe { dao.getAllTenantsMatching(matching, order) }
    }

    override suspend fun getAllWithoutUpcomingStaysMatching(
        matching: String?,
        currentTenantId: Long?,
    ): AppResult<List<TenantCompound>, InfraError> {
        return onDatabaseSafe {
            dao.getAllWithoutUpcomingStaysMatching(
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

    override fun watchById(id: Long): AppResult<Flow<TenantCompound?>, InfraError> {
        return onDatabaseSafe { dao.watchById(id) }
    }

    override fun watchAll(
        tenantStatusIdFilter: Int?,
        matching: String?,
        orderTypeId: Int?,
    ): AppResult<Flow<List<TenantCompound>>, InfraError> {
        return onDatabaseSafe {
            dao.watchAll(
                tenantStatusIdFilter = tenantStatusIdFilter,
                matching = matching,
                orderTypeId = orderTypeId,
            )
        }
    }

    override suspend fun countTenantsWithName(
        tenantName: String,
        currentStoredTenantName: String?,
    ): AppResult<Int, InfraError> {
        return onDatabaseSafe {
            dao.countTenantsWithName(
                tenantName = tenantName,
                currentStoredTenantName = currentStoredTenantName,
            )
        }
    }
}
