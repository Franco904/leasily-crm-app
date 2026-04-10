package com.fstengineering.leasilycrm.propertyDashboard.application.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult

interface TenantQueryRepository {
    suspend fun getNameById(tenantId: Long): AppResult<String, ApplicationError>
}
