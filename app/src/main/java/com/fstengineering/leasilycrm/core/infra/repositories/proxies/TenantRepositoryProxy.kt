package com.fstengineering.leasilycrm.core.infra.repositories.proxies

import com.fstengineering.leasilycrm.core.application.repositories.LeadTenantSharedCommandRepository
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.entities.Tenant
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.domain.valueObjects.TenantStatus
import com.fstengineering.leasilycrm.core.infra.dataSources.inMemory.interfaces.TenantInMemoryDataSource

class TenantRepositoryProxy(
    private val tenantInMemoryDataSource: TenantInMemoryDataSource,
    private val leadTenantSharedCommandRepository: LeadTenantSharedCommandRepository,
) : LeadTenantSharedCommandRepository {
    override suspend fun delete(id: Long): AppResult<Unit, ApplicationError> {
        return leadTenantSharedCommandRepository.delete(id)
            .onSuccess { tenantInMemoryDataSource.removeTenant(id = id) }
    }

    override suspend fun getById(tenantId: Long): AppResult<Tenant, ApplicationError> {
        val inMemoryTenant = tenantInMemoryDataSource.getTenantById(tenantId)
        if (inMemoryTenant != null) return AppResult.Success(data = inMemoryTenant)

        return leadTenantSharedCommandRepository.getById(tenantId)
            .onSuccess { tenant -> tenantInMemoryDataSource.putTenant(tenant) }
    }

    override suspend fun getAllWithoutUpcomingStaysMatching(
        matching: String?,
        currentTenantId: Long?
    ) =
        leadTenantSharedCommandRepository.getAllWithoutUpcomingStaysMatching(
            matching,
            currentTenantId
        )

    override suspend fun getPreferredLeadsOrderTypeId() =
        leadTenantSharedCommandRepository.getPreferredLeadsOrderTypeId()

    override suspend fun getPreferredTenantsOrderTypeId() =
        leadTenantSharedCommandRepository.getPreferredTenantsOrderTypeId()

    override suspend fun updatePreferredLeadsOrderTypeId(orderTypeId: Int) =
        leadTenantSharedCommandRepository.updatePreferredLeadsOrderTypeId(orderTypeId)

    override suspend fun updatePreferredTenantsOrderTypeId(orderTypeId: Int) =
        leadTenantSharedCommandRepository.updatePreferredTenantsOrderTypeId(orderTypeId)

    override fun watchById(tenantId: Long) = leadTenantSharedCommandRepository.watchById(tenantId)

    override fun watchAll(
        tenantStatusFilter: TenantStatus?,
        matching: String?,
        orderTypeId: Int?,
    ) =
        leadTenantSharedCommandRepository.watchAll(tenantStatusFilter, matching, orderTypeId)
}
