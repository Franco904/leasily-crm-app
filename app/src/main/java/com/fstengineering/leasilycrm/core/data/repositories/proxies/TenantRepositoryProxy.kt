package com.fstengineering.leasilycrm.core.data.repositories.proxies

import com.fstengineering.leasilycrm.core.data.dataSources.cache.interfaces.CacheLocalDataSource
import com.fstengineering.leasilycrm.core.domain.models.Tenant
import com.fstengineering.leasilycrm.core.domain.models.enums.TenantStatus
import com.fstengineering.leasilycrm.core.domain.repositories.TenantRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class TenantRepositoryProxy(
    private val cacheLocalDataSource: CacheLocalDataSource,
    private val tenantRepository: TenantRepository,
) : TenantRepository {
    override suspend fun insertOrUpdate(tenant: Tenant): AppResult<Unit, DataSourceError> {
        return tenantRepository.insertOrUpdate(tenant)
            .onSuccess { cacheLocalDataSource.removeWhere<Tenant> { it?.id == tenant.id } }
    }

    override suspend fun delete(id: Long): AppResult<Unit, DataSourceError> {
        return tenantRepository.delete(id)
    }

    override suspend fun getById(tenantId: Long): AppResult<Tenant, DataSourceError> {
        val cachedTenant = cacheLocalDataSource.get<Tenant>(key = tenantId.toString())
        if (cachedTenant != null) return AppResult.Success(data = cachedTenant)

        return tenantRepository.getById(tenantId)
            .onSuccess { tenant -> cacheLocalDataSource.put(key = tenantId.toString(), value = tenant) }
    }

    override suspend fun getAllLeadsMatching(
        matching: String?,
        orderTypeId: Int
    ): AppResult<ImmutableList<Tenant>, DataSourceError> {
        return tenantRepository.getAllLeadsMatching(matching, orderTypeId)
    }

    override suspend fun getAllTenantsMatching(
        matching: String?,
        orderTypeId: Int
    ): AppResult<ImmutableList<Tenant>, DataSourceError> {
        return tenantRepository.getAllTenantsMatching(matching, orderTypeId)
    }

    override suspend fun getAllWithoutUpcomingStaysMatching(
        matching: String?,
        currentTenantId: Long?
    ): AppResult<ImmutableList<Tenant>, DataSourceError> {
        return tenantRepository.getAllWithoutUpcomingStaysMatching(matching, currentTenantId)
    }

    override suspend fun getAllCityNames(): AppResult<ImmutableList<String>, DataSourceError> {
        return tenantRepository.getAllCityNames()
    }

    override suspend fun getPreferredLeadsOrderTypeId(): AppResult<Int, DataSourceError> {
        return tenantRepository.getPreferredLeadsOrderTypeId()
    }

    override suspend fun getPreferredTenantsOrderTypeId(): AppResult<Int, DataSourceError> {
        return tenantRepository.getPreferredTenantsOrderTypeId()
    }

    override suspend fun updatePreferredLeadsOrderTypeId(orderTypeId: Int): AppResult<Unit, DataSourceError> {
        return tenantRepository.updatePreferredLeadsOrderTypeId(orderTypeId)
    }

    override suspend fun updatePreferredTenantsOrderTypeId(orderTypeId: Int): AppResult<Unit, DataSourceError> {
        return tenantRepository.updatePreferredTenantsOrderTypeId(orderTypeId)
    }

    override fun watchById(tenantId: Long): Flow<AppResult<Tenant?, DataSourceError>> {
        return tenantRepository.watchById(tenantId)
    }

    override fun watchAll(
        tenantStatusFilter: TenantStatus?,
        matching: String?,
        orderTypeId: Int?,
    ): Flow<AppResult<ImmutableList<Tenant>, DataSourceError>> {
        return tenantRepository.watchAll(tenantStatusFilter, matching, orderTypeId)
    }

    override suspend fun isThereTenantWithName(
        tenantName: String,
        currentStoredTenantName: String?
    ): AppResult<Boolean, DataSourceError> {
        return tenantRepository.isThereTenantWithName(tenantName, currentStoredTenantName)
    }
}
