package com.fstengineering.leasilycrm.core.infra.repositories

import com.fstengineering.leasilycrm.core.application.repositories.AssistanceSharedCommandRepository
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.infra.adapters.interfaces.DatabaseAdapter
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.AssistanceDatabaseDataSource

class AssistanceSharedCommandRepositoryImpl(
    private val databaseAdapter: DatabaseAdapter,
    private val assistanceDatabaseDataSource: AssistanceDatabaseDataSource,
) : AssistanceSharedCommandRepository {
    override suspend fun finishOpenAssistanceByTenantId(tenantId: Long): AppResult<Unit, ApplicationError> {
        return databaseAdapter.withTransaction {
            assistanceDatabaseDataSource.finishOpenAssistanceByTenantId(tenantId)
        }
            .fold(
                onError = { AppResult.Error(error = it.toApplicationError()) },
                onSuccess = { AppResult.Success(data = Unit) }
            )
    }

    override suspend fun unfinishAssistanceByTenantId(tenantId: Long): AppResult<Unit, ApplicationError> {
        return databaseAdapter.withTransaction {
            assistanceDatabaseDataSource.unfinishAssistanceByTenantId(tenantId)
        }
            .fold(
                onError = { AppResult.Error(error = it.toApplicationError()) },
                onSuccess = { AppResult.Success(data = Unit) }
            )
    }
}