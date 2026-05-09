package com.fstengineering.leasilycrm.leadTenantDetails.application.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.leadTenantDetails.application.models.dtos.LeadTenantRawReadModel
import kotlinx.coroutines.flow.Flow

interface LeadTenantQueryRepository {
    fun watchById(id: Long): Flow<AppResult<LeadTenantRawReadModel?, ApplicationError>>
}
