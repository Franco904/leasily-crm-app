package com.fstengineering.leasilycrm.core.infra.dataSources.database

import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.TenantDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.TenantEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.TenantCompound
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.TenantDatabaseDataSource
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import kotlinx.coroutines.flow.Flow

class TenantDatabaseDataSourceImpl(
    private val dao: TenantDao,
) : TenantDatabaseDataSource {
    override suspend fun insertOrUpdate(tenant: TenantEntity): Long =
        dao.insertOrUpdate(tenant)

    override suspend fun delete(id: Long) =
        dao.delete(id)

    override suspend fun getById(id: Long): AppResult<TenantCompound?, DataError> {
        return onDatabaseSafe { dao.getById(id) }
    }

    override suspend fun getAllLeadsMatching(
        userId: Long,
        matching: String?,
        order: Int,
    ): AppResult<List<TenantCompound>, DataError> {
        return onDatabaseSafe { dao.getAllLeadsMatching(userId, matching, order) }
    }

    override suspend fun getAllTenantsMatching(
        userId: Long,
        matching: String?,
        order: Int,
    ): AppResult<List<TenantCompound>, DataError> {
        return onDatabaseSafe { dao.getAllTenantsMatching(userId, matching, order) }
    }

    override suspend fun getAllWithoutUpcomingStaysMatching(
        userId: Long,
        matching: String?,
        currentTenantId: Long?,
    ): AppResult<List<TenantCompound>, DataError> {
        return onDatabaseSafe {
            dao.getAllWithoutUpcomingStaysMatching(
                userId = userId,
                matching = matching,
                currentTenantId = currentTenantId,
            )
        }
    }

    override suspend fun getNameById(id: Long): AppResult<String, DataError> {
        return onDatabaseSafe { dao.getNameById(id) }
    }

    override suspend fun getAllTagNames(userId: Long): AppResult<List<String>, DataError> {
        return onDatabaseSafe { dao.getAllTagNames(userId) }
    }

    override suspend fun getAllCityNames(userId: Long): AppResult<List<String>, DataError> {
        return onDatabaseSafe { dao.getAllCityNames(userId) }
    }

    override fun watchById(id: Long): AppResult<Flow<TenantCompound?>, DataError> {
        return onDatabaseSafe { dao.watchById(id) }
    }

    override fun watchAll(
        userId: Long,
        tenantStatusIdFilter: Int?,
        matching: String?,
        orderTypeId: Int?,
    ): AppResult<Flow<List<TenantCompound>>, DataError> {
        return onDatabaseSafe {
            dao.watchAll(
                userId = userId,
                tenantStatusIdFilter = tenantStatusIdFilter,
                matching = matching,
                orderTypeId = orderTypeId,
            )
        }
    }

    override suspend fun countTenantsWithName(
        userId: Long,
        tenantName: String,
        currentStoredTenantName: String?,
    ): AppResult<Int, DataError> {
        return onDatabaseSafe {
            dao.countTenantsWithName(
                userId = userId,
                tenantName = tenantName,
                currentStoredTenantName = currentStoredTenantName,
            )
        }
    }
}
