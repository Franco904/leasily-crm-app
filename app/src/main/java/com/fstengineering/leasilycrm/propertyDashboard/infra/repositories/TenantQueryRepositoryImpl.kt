package com.fstengineering.leasilycrm.propertyDashboard.infra.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.TenantDatabaseDataSource
import com.fstengineering.leasilycrm.propertyDashboard.application.repositories.TenantQueryRepository

class TenantQueryRepositoryImpl(
    private val databaseDataSource: TenantDatabaseDataSource,
) : TenantQueryRepository {
    override suspend fun getNameById(tenantId: Long): AppResult<String, ApplicationError> {
        return databaseDataSource.getNameById(tenantId)
            .fold(
                onError = { AppResult.Error(error = it.toApplicationError()) },
                onSuccess = { AppResult.Success(data = it) }
            )
    }
}
