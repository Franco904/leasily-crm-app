package com.fstengineering.leasilycrm.assistanceRegistration.application.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult

interface LeadTenantQueryRepository {
    suspend fun getNameById(tenantId: Long): AppResult<String, ApplicationError>
}
