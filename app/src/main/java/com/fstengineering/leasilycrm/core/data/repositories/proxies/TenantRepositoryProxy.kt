package com.fstengineering.leasilycrm.core.data.repositories.proxies

import com.fstengineering.leasilycrm.core.data.dataSources.inMemory.interfaces.TenantInMemoryDataSource
import com.fstengineering.leasilycrm.core.domain.models.Tenant
import com.fstengineering.leasilycrm.core.domain.models.enums.TenantStatus
import com.fstengineering.leasilycrm.core.domain.repositories.TenantRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

class TenantRepositoryProxy(
    private val tenantInMemoryDataSource: TenantInMemoryDataSource,
    private val tenantRepository: TenantRepository,
) : TenantRepository {
    override suspend fun insertOrUpdate(tenant: Tenant): AppResult<Unit, DataError> {
        return tenantRepository.insertOrUpdate(tenant)
            .onSuccess { tenantInMemoryDataSource.removeTenant(tenant) }
    }

    override suspend fun delete(id: Long): AppResult<Unit, DataError> {
        return tenantRepository.delete(id)
    }

    override suspend fun getById(tenantId: Long): AppResult<Tenant, DataError> {
        val cachedTenant = tenantInMemoryDataSource.getTenantById(tenantId)
        if (cachedTenant != null) return AppResult.Success(data = cachedTenant)

        return tenantRepository.getById(tenantId)
            .onSuccess { tenant -> tenantInMemoryDataSource.putTenant(tenant) }
    }

    override suspend fun getAllLeadsMatching(
        matching: String?,
        orderTypeId: Int
    ): AppResult<ImmutableList<Tenant>, DataError> {
        return tenantRepository.getAllLeadsMatching(matching, orderTypeId)
    }

    override suspend fun getAllTenantsMatching(
        matching: String?,
        orderTypeId: Int
    ): AppResult<ImmutableList<Tenant>, DataError> {
        return tenantRepository.getAllTenantsMatching(matching, orderTypeId)
    }

    override suspend fun getAllWithoutUpcomingStaysMatching(
        matching: String?,
        currentTenantId: Long?
    ): AppResult<ImmutableList<Tenant>, DataError> {
        return tenantRepository.getAllWithoutUpcomingStaysMatching(matching, currentTenantId)
    }

    override suspend fun getAllCityNames(): AppResult<ImmutableList<String>, DataError> {
        return tenantRepository.getAllCityNames()
    }

    override suspend fun getPreferredLeadsOrderTypeId(): AppResult<Int, DataError> {
        return tenantRepository.getPreferredLeadsOrderTypeId()
    }

    override suspend fun getPreferredTenantsOrderTypeId(): AppResult<Int, DataError> {
        return tenantRepository.getPreferredTenantsOrderTypeId()
    }

    override suspend fun updatePreferredLeadsOrderTypeId(orderTypeId: Int): AppResult<Unit, DataError> {
        return tenantRepository.updatePreferredLeadsOrderTypeId(orderTypeId)
    }

    override suspend fun updatePreferredTenantsOrderTypeId(orderTypeId: Int): AppResult<Unit, DataError> {
        return tenantRepository.updatePreferredTenantsOrderTypeId(orderTypeId)
    }

    override fun watchById(tenantId: Long): Flow<AppResult<Tenant?, DataError>> {
        return tenantRepository.watchById(tenantId)
    }

    override fun watchAll(
        tenantStatusFilter: TenantStatus?,
        matching: String?,
        orderTypeId: Int?,
    ): Flow<AppResult<ImmutableList<Tenant>, DataError>> {
        return tenantRepository.watchAll(tenantStatusFilter, matching, orderTypeId)
    }

    override suspend fun isThereTenantWithName(
        tenantName: String,
        currentStoredTenantName: String?
    ): AppResult<Boolean, DataError> {
        return tenantRepository.isThereTenantWithName(tenantName, currentStoredTenantName)
    }
}
