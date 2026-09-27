package com.fstengineering.leasilycrm.core.infra.repositories

import com.fstengineering.leasilycrm.core.application.repositories.LeadTenantSharedQueryRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.TenantDatabaseDataSource

class LeadTenantSharedQueryRepositoryImpl(
    private val databaseDataSource: TenantDatabaseDataSource,
) : LeadTenantSharedQueryRepository {
    override suspend fun getNameById(tenantId: Long): AppResult<String, ApplicationError> {
        return databaseDataSource.getNameById(tenantId)
    }
}
