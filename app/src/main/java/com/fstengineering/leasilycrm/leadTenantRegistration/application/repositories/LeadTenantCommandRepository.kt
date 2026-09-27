package com.fstengineering.leasilycrm.leadTenantRegistration.application.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.leadTenantRegistration.domain.entities.LeadTenant

interface LeadTenantCommandRepository {
    suspend fun insertOrUpdate(leadTenant: LeadTenant): AppResult<Unit, ApplicationError>
}
