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

    fun watchById(tenantId: Long): Flow<AppResult<Tenant, DataSourceError>>

    fun watchAll(): Flow<AppResult<ImmutableList<Tenant>, DataSourceError>>
}
