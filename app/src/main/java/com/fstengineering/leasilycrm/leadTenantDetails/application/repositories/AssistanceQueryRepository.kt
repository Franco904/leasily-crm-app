package com.fstengineering.leasilycrm.leadTenantDetails.application.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.leadTenantDetails.application.models.dtos.AssistanceReadModel

interface AssistanceQueryRepository {
    suspend fun getAllByLeadTenantId(leadTenantId: Long): AppResult<List<AssistanceReadModel>, ApplicationError>
}
