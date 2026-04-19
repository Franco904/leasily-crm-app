package com.fstengineering.leasilycrm.leadTenantDetails.application.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult

interface AssistanceCommandRepository {
    suspend fun delete(id: Long): AppResult<Unit, ApplicationError>
}
