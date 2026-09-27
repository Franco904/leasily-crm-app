package com.fstengineering.leasilycrm.assistanceRegistration.infra.repositories

import com.fstengineering.leasilycrm.assistanceRegistration.application.repositories.LeadTenantQueryRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.TenantDatabaseDataSource

class LeadTenantQueryRepositoryImpl(
    private val databaseDataSource: TenantDatabaseDataSource,
) : LeadTenantQueryRepository {
    override suspend fun getNameById(tenantId: Long): AppResult<String, ApplicationError> {
        return databaseDataSource.getNameById(id = tenantId)
    }
}
