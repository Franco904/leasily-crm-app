package com.fstengineering.leasilycrm.leadTenantDetails.application.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.leadTenantDetails.application.models.dtos.StayReadModel

interface StayQueryRepository {
    suspend fun getAllByTenantId(tenantId: Long): AppResult<List<StayReadModel>, ApplicationError>
}
