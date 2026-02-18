package com.fstengineering.leasilycrm.core.application.repositories

import com.fstengineering.leasilycrm.core.domain.entities.Tenant
import com.fstengineering.leasilycrm.core.domain.valueObjects.TenantStatus
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

interface TenantRepository {
    suspend fun insertOrUpdate(
        tenant: Tenant,
    ): AppResult<Unit, DataError>

    suspend fun delete(id: Long): AppResult<Unit, DataError>

    suspend fun getById(tenantId: Long): AppResult<Tenant, DataError>

    suspend fun getAllLeadsMatching(
        matching: String?,
        orderTypeId: Int,
    ): AppResult<ImmutableList<Tenant>, DataError>

    suspend fun getAllTenantsMatching(
        matching: String?,
        orderTypeId: Int,
    ): AppResult<ImmutableList<Tenant>, DataError>

    suspend fun getAllWithoutUpcomingStaysMatching(
        matching: String?,
        currentTenantId: Long?,
    ): AppResult<ImmutableList<Tenant>, DataError>

    suspend fun getAllTagNames(): AppResult<ImmutableList<String>, DataError>

    suspend fun getAllCityNames(): AppResult<ImmutableList<String>, DataError>

    suspend fun getPreferredLeadsOrderTypeId(): AppResult<Int, DataError>

    suspend fun getPreferredTenantsOrderTypeId(): AppResult<Int, DataError>

    suspend fun updatePreferredLeadsOrderTypeId(orderTypeId: Int): AppResult<Unit, DataError>

    suspend fun updatePreferredTenantsOrderTypeId(orderTypeId: Int): AppResult<Unit, DataError>

    fun watchById(tenantId: Long): Flow<AppResult<Tenant?, DataError>>

    fun watchAll(
        tenantStatusFilter: TenantStatus? = null,
        matching: String? = null,
        orderTypeId: Int? = null,
    ): Flow<AppResult<ImmutableList<Tenant>, DataError>>

    suspend fun isThereTenantWithName(
        tenantName: String,
        currentStoredTenantName: String?,
    ): AppResult<Boolean, DataError>
}
