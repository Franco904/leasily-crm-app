package com.fstengineering.leasilycrm.core.data.dataSources.inMemory

import com.fstengineering.leasilycrm.core.data.dataSources.inMemory.interfaces.InMemoryDataSource
import com.fstengineering.leasilycrm.core.data.dataSources.inMemory.interfaces.TenantInMemoryDataSource
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

    override fun removeTenant(tenant: Tenant) {
        inMemoryDataSource.removeWhere<Tenant> { t -> t?.id == tenant.id }
    }
}
