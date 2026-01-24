package com.fstengineering.leasilycrm.core.infra.dataSources.inMemory.interfaces

import com.fstengineering.leasilycrm.core.domain.models.Tenant

interface TenantInMemoryDataSource {
    fun getTenantById(id: Long): Tenant?

    fun putTenant(tenant: Tenant)

    fun removeTenant(id: Long?)
}
