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

    suspend fun getAllLeadsMatching(
        matching: String?,
        orderTypeId: Int,
    ): AppResult<ImmutableList<Tenant>, DataSourceError>

    suspend fun getAllTenantsMatching(
        matching: String?,
        orderTypeId: Int,
    ): AppResult<ImmutableList<Tenant>, DataSourceError>

    suspend fun getAllCityNames(): AppResult<ImmutableList<String>, DataSourceError>

    suspend fun getPreferredLeadsOrderTypeId(): AppResult<Int, DataSourceError>

    suspend fun getPreferredTenantsOrderTypeId(): AppResult<Int, DataSourceError>

    suspend fun updatePreferredLeadsOrderTypeId(orderTypeId: Int): AppResult<Unit, DataSourceError>

    suspend fun updatePreferredTenantsOrderTypeId(orderTypeId: Int): AppResult<Unit, DataSourceError>

    fun watchById(tenantId: Long): Flow<AppResult<Tenant, DataSourceError>>

    fun watchAllLeads(orderTypeId: Int): Flow<AppResult<ImmutableList<Tenant>, DataSourceError>>

    fun watchAllTenants(
        orderTypeId: Int,
        tenantFilterType: Int,
    ): Flow<AppResult<ImmutableList<Tenant>, DataSourceError>>
}
