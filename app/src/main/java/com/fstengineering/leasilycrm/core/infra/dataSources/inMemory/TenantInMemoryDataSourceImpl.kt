package com.fstengineering.leasilycrm.core.infra.dataSources.inMemory

import com.fstengineering.leasilycrm.core.infra.dataSources.inMemory.interfaces.InMemoryDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.inMemory.interfaces.TenantInMemoryDataSource
import com.fstengineering.leasilycrm.core.domain.models.Tenant

class TenantInMemoryDataSourceImpl(
    private val inMemoryDataSource: InMemoryDataSource,
) : TenantInMemoryDataSource {
    override fun getTenantById(id: Long): Tenant? {
        return inMemoryDataSource.get<Tenant>(key = id.toString())
    }

    override fun putTenant(tenant: Tenant) {
        inMemoryDataSource.put(key = tenant.id.toString(), value = tenant)
    }

    override fun removeTenant(id: Long?) {
        if (id == null) return
        inMemoryDataSource.removeWhere<Tenant> { t -> t?.id == id }
    }
}
