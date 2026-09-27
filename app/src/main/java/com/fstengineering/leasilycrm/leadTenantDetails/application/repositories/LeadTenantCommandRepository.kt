package com.fstengineering.leasilycrm.leadTenantDetails.application.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError

interface LeadTenantCommandRepository {
    suspend fun delete(id: Long): AppResult<Unit, ApplicationError>
}
