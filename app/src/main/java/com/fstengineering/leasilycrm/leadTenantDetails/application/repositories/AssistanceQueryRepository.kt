package com.fstengineering.leasilycrm.leadTenantDetails.application.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.leadTenantDetails.application.models.dtos.AssistanceRawReadModel
import kotlinx.collections.immutable.ImmutableList

interface AssistanceQueryRepository {
    suspend fun getAllByLeadTenantId(
        leadTenantId: Long,
    ): AppResult<ImmutableList<AssistanceRawReadModel>, ApplicationError>
}
