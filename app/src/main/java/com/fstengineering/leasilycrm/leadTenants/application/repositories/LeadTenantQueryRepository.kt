package com.fstengineering.leasilycrm.leadTenants.application.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.valueObjects.TenantStatus
import com.fstengineering.leasilycrm.leadTenants.application.models.dtos.LeadTenantListItemReadModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

interface LeadTenantQueryRepository {
    fun watchAll(
        tenantStatusFilter: TenantStatus? = null,
        matching: String? = null,
        orderTypeId: Int? = null,
    ): Flow<AppResult<ImmutableList<LeadTenantListItemReadModel>, ApplicationError>>

    suspend fun getAllLeadsMatching(
        matching: String?,
        orderTypeId: Int,
    ): AppResult<ImmutableList<LeadTenantListItemReadModel>, ApplicationError>

    suspend fun getPreferredLeadsOrderTypeId(): AppResult<Int, ApplicationError>

    suspend fun getPreferredTenantsOrderTypeId(): AppResult<Int, ApplicationError>

    suspend fun updatePreferredLeadsOrderTypeId(orderTypeId: Int): AppResult<Unit, ApplicationError>

    suspend fun updatePreferredTenantsOrderTypeId(orderTypeId: Int): AppResult<Unit, ApplicationError>
}
