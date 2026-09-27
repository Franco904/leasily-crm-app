package com.fstengineering.leasilycrm.core.infra.repositories

import com.fstengineering.leasilycrm.core.application.repositories.AssistanceSharedCommandRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.infra.adapters.interfaces.DatabaseAdapter
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.AssistanceDatabaseDataSource

class AssistanceSharedCommandRepositoryImpl(
    private val databaseAdapter: DatabaseAdapter,
    private val assistanceDatabaseDataSource: AssistanceDatabaseDataSource,
) : AssistanceSharedCommandRepository {
    override suspend fun finishOpenAssistanceByTenantId(tenantId: Long): AppResult<Unit, ApplicationError> {
        return databaseAdapter.transaction {
            assistanceDatabaseDataSource.finishOpenAssistanceByTenantId(tenantId)
        }
    }

    override suspend fun unfinishAssistanceByTenantId(tenantId: Long): AppResult<Unit, ApplicationError> {
        return databaseAdapter.transaction {
            assistanceDatabaseDataSource.unfinishAssistanceByTenantId(tenantId)
        }
    }
}
