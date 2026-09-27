package com.fstengineering.leasilycrm.leadTenantDetails.infra.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.infra.adapters.interfaces.DatabaseAdapter
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.AssistanceDatabaseDataSource
import com.fstengineering.leasilycrm.leadTenantDetails.application.repositories.AssistanceCommandRepository

class AssistanceCommandRepositoryImpl(
    private val databaseAdapter: DatabaseAdapter,
    private val assistanceDatabaseDataSource: AssistanceDatabaseDataSource,
) : AssistanceCommandRepository {
    override suspend fun delete(id: Long): AppResult<Unit, ApplicationError> {
        return databaseAdapter.transaction {
            assistanceDatabaseDataSource.delete(id)
        }
            .fold(
                onError = { AppResult.Error(error = it.toApplicationError()) },
                onSuccess = { AppResult.Success(data = Unit) },
            )
    }
}
