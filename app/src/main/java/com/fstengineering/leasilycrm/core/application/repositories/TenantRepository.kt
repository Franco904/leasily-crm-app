package com.fstengineering.leasilycrm.core.application.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.entities.Tenant
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.valueObjects.TenantStatus
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

interface TenantRepository {
    suspend fun delete(id: Long): AppResult<Unit, ApplicationError>

    suspend fun getById(tenantId: Long): AppResult<Tenant, ApplicationError>

    suspend fun getNameById(tenantId: Long): AppResult<String, ApplicationError>

    suspend fun getAllLeadsMatching(
        matching: String?,
        orderTypeId: Int,
    ): AppResult<ImmutableList<Tenant>, ApplicationError>

    suspend fun getAllTenantsMatching(
        matching: String?,
        orderTypeId: Int,
    ): AppResult<ImmutableList<Tenant>, ApplicationError>

    suspend fun getAllWithoutUpcomingStaysMatching(
        matching: String?,
        currentTenantId: Long?,
    ): AppResult<ImmutableList<Tenant>, ApplicationError>

    suspend fun getPreferredLeadsOrderTypeId(): AppResult<Int, ApplicationError>

    suspend fun getPreferredTenantsOrderTypeId(): AppResult<Int, ApplicationError>

    suspend fun updatePreferredLeadsOrderTypeId(orderTypeId: Int): AppResult<Unit, ApplicationError>

    suspend fun updatePreferredTenantsOrderTypeId(orderTypeId: Int): AppResult<Unit, ApplicationError>

    fun watchById(tenantId: Long): Flow<AppResult<Tenant?, ApplicationError>>

    fun watchAll(
        tenantStatusFilter: TenantStatus? = null,
        matching: String? = null,
        orderTypeId: Int? = null,
    ): Flow<AppResult<ImmutableList<Tenant>, ApplicationError>>
}
