package com.fstengineering.leasilycrm.core.domain.repositories

import com.fstengineering.leasilycrm.core.domain.models.Tenant
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

interface TenantRepository {
    suspend fun insertOrUpdate(
        tenant: Tenant,
    ): AppResult<Unit, DataSourceError>

    suspend fun delete(id: Long): AppResult<Unit, DataSourceError>

    suspend fun getById(tenantId: Long): AppResult<Tenant, DataSourceError>

    suspend fun getNameById(tenantId: Long): AppResult<String, DataSourceError>

    suspend fun getAllCityNames(): AppResult<ImmutableList<String>, DataSourceError>

    suspend fun getPreferredLeadsOrdenationTypeId(): AppResult<Int, DataSourceError>

    suspend fun getPreferredTenantsOrdenationTypeId(): AppResult<Int, DataSourceError>

    suspend fun updatePreferredLeadsOrdenationTypeId(ordenationTypeId: Int): AppResult<Unit, DataSourceError>

    suspend fun updatePreferredTenantsOrdenationTypeId(ordenationTypeId: Int): AppResult<Unit, DataSourceError>

    fun watchById(tenantId: Long): Flow<AppResult<Tenant, DataSourceError>>

    fun watchAllLeads(ordenationTypeId: Int): Flow<AppResult<ImmutableList<Tenant>, DataSourceError>>

    fun watchAllTenants(
        ordenationTypeId: Int,
        tenantFilterType: Int,
    ): Flow<AppResult<ImmutableList<Tenant>, DataSourceError>>
}
