package com.fstengineering.leasilycrm.core.application.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError

interface LeadTenantSharedQueryRepository {
    suspend fun getNameById(tenantId: Long): AppResult<String, ApplicationError>
}
