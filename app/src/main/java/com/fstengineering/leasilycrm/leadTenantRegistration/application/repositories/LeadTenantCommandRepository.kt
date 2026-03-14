package com.fstengineering.leasilycrm.leadTenantRegistration.application.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.entities.Tenant
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.infra.adapters.interfaces.DatabaseAdapter
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.TenantEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.TenantDatabaseDataSource
import com.fstengineering.leasilycrm.leadTenantRegistration.domain.entities.LeadTenant
import com.fstengineering.leasilycrm.propertyOwnerRegistration.domain.entities.PropertyOwner

interface LeadTenantCommandRepository {
    suspend fun insertOrUpdate(leadTenant: LeadTenant): AppResult<Unit, ApplicationError>
}
