package com.fstengineering.leasilycrm.core.data.repositories.proxies

import com.fstengineering.leasilycrm.core.infra.dataSources.inMemory.interfaces.TenantInMemoryDataSource
import com.fstengineering.leasilycrm.core.domain.models.Tenant
import com.fstengineering.leasilycrm.core.domain.models.enums.TenantStatus
import com.fstengineering.leasilycrm.core.domain.repositories.TenantRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess

class TenantRepositoryProxy(
    private val tenantInMemoryDataSource: TenantInMemoryDataSource,
    private val tenantRepository: TenantRepository,
) : TenantRepository {
    override suspend fun insertOrUpdate(tenant: Tenant): AppResult<Unit, DataError> {
        return tenantRepository.insertOrUpdate(tenant)
            .onSuccess { tenantInMemoryDataSource.removeTenant(tenant.id) }
    }

    override suspend fun delete(id: Long): AppResult<Unit, DataError> {
        return tenantRepository.delete(id)
            .onSuccess { tenantInMemoryDataSource.removeTenant(id = id) }
    }

    override suspend fun getById(tenantId: Long): AppResult<Tenant, DataError> {
        val inMemoryTenant = tenantInMemoryDataSource.getTenantById(tenantId)
        if (inMemoryTenant != null) return AppResult.Success(data = inMemoryTenant)

        return tenantRepository.getById(tenantId)
            .onSuccess { tenant -> tenantInMemoryDataSource.putTenant(tenant) }
    }

    override suspend fun getAllLeadsMatching(
        matching: String?,
        orderTypeId: Int
    ) =
        tenantRepository.getAllLeadsMatching(matching, orderTypeId)

    override suspend fun getAllTenantsMatching(
        matching: String?,
        orderTypeId: Int
    ) =
        tenantRepository.getAllTenantsMatching(matching, orderTypeId)

    override suspend fun getAllWithoutUpcomingStaysMatching(
        matching: String?,
        currentTenantId: Long?
    ) =
        tenantRepository.getAllWithoutUpcomingStaysMatching(matching, currentTenantId)

    override suspend fun getAllTagNames() = tenantRepository.getAllTagNames()

    override suspend fun getAllCityNames() = tenantRepository.getAllCityNames()

    override suspend fun getPreferredLeadsOrderTypeId() =
        tenantRepository.getPreferredLeadsOrderTypeId()

    override suspend fun getPreferredTenantsOrderTypeId() =
        tenantRepository.getPreferredTenantsOrderTypeId()

    override suspend fun updatePreferredLeadsOrderTypeId(orderTypeId: Int) =
        tenantRepository.updatePreferredLeadsOrderTypeId(orderTypeId)

    override suspend fun updatePreferredTenantsOrderTypeId(orderTypeId: Int) =
        tenantRepository.updatePreferredTenantsOrderTypeId(orderTypeId)

    override fun watchById(tenantId: Long) = tenantRepository.watchById(tenantId)

    override fun watchAll(
        tenantStatusFilter: TenantStatus?,
        matching: String?,
        orderTypeId: Int?,
    ) =
        tenantRepository.watchAll(tenantStatusFilter, matching, orderTypeId)

    override suspend fun isThereTenantWithName(
        tenantName: String,
        currentStoredTenantName: String?
    ) =
        tenantRepository.isThereTenantWithName(tenantName, currentStoredTenantName)
}
