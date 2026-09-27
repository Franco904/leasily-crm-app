package com.fstengineering.leasilycrm.leadTenantRegistration.infra.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.infra.adapters.interfaces.DatabaseAdapter
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.TenantDatabaseDataSource
import com.fstengineering.leasilycrm.leadTenantRegistration.application.repositories.LeadTenantCommandRepository
import com.fstengineering.leasilycrm.leadTenantRegistration.domain.entities.LeadTenant
import com.fstengineering.leasilycrm.leadTenantRegistration.infra.mappers.toLocalDataModel

class LeadTenantCommandRepositoryImpl(
    private val databaseAdapter: DatabaseAdapter,
    private val databaseDataSource: TenantDatabaseDataSource,
) : LeadTenantCommandRepository {
    override suspend fun insertOrUpdate(leadTenant: LeadTenant): AppResult<Unit, ApplicationError> {
        val tenantLocalModel = leadTenant.toLocalDataModel()

        return databaseAdapter.transaction {
            databaseDataSource.insertOrUpdate(tenantLocalModel)
        }
            .fold(
                onError = { AppResult.Error(error = it.toApplicationError()) },
                onSuccess = { AppResult.Success(data = Unit) }
            )
    }
}
