package com.fstengineering.leasilycrm.home.infra.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.infra.adapters.interfaces.DatabaseAdapter
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.TenantDatabaseDataSource
import com.fstengineering.leasilycrm.home.application.repositories.LeadTenantCommandRepository

class LeadTenantCommandRepositoryImpl(
    private val databaseAdapter: DatabaseAdapter,
    private val tenantDatabaseDataSource: TenantDatabaseDataSource,
) : LeadTenantCommandRepository {
    override suspend fun delete(id: Long): AppResult<Unit, ApplicationError> {
        return databaseAdapter.transaction {
            tenantDatabaseDataSource.delete(id)
        }
    }
}
