package com.fstengineering.leasilycrm.assistanceRegistration.application.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError

interface LeadTenantQueryRepository {
    suspend fun getNameById(tenantId: Long): AppResult<String, ApplicationError>
}
