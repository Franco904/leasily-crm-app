package com.fstengineering.leasilycrm.core.application.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError

interface AssistanceSharedCommandRepository {
    suspend fun finishOpenAssistanceByTenantId(tenantId: Long): AppResult<Unit, ApplicationError>

    suspend fun unfinishAssistanceByTenantId(tenantId: Long): AppResult<Unit, ApplicationError>
}