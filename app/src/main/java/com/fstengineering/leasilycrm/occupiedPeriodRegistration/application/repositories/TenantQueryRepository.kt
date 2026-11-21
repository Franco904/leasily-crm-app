package com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.dtos.TenantReadModel
import kotlinx.collections.immutable.ImmutableList

interface TenantQueryRepository {
    suspend fun getById(tenantId: Long): AppResult<TenantReadModel>

    suspend fun getAllWithoutUpcomingStays(
        matching: String?,
        currentTenantId: Long?,
    ): AppResult<ImmutableList<TenantReadModel>>
}
