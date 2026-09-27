package com.fstengineering.leasilycrm.occupiedPeriodDetails.application.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.occupiedPeriodDetails.application.models.dtos.TenantReadModel

interface TenantQueryRepository {
    suspend fun getById(tenantId: Long): AppResult<TenantReadModel, ApplicationError>
}
