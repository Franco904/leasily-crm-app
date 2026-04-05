package com.fstengineering.leasilycrm.stayRegistration.application.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.stayRegistration.application.models.TenantReadModel
import kotlinx.collections.immutable.ImmutableList

interface TenantQueryRepository {
    suspend fun getById(tenantId: Long): AppResult<TenantReadModel, ApplicationError>

    suspend fun getAllWithoutUpcomingStays(
        matching: String?,
        currentTenantId: Long?,
    ): AppResult<ImmutableList<TenantReadModel>, ApplicationError>
}
