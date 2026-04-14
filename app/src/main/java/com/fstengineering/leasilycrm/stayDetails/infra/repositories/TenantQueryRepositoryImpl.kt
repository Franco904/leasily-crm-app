package com.fstengineering.leasilycrm.stayDetails.infra.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.TenantDatabaseDataSource
import com.fstengineering.leasilycrm.stayDetails.application.models.dtos.TenantReadModel
import com.fstengineering.leasilycrm.stayDetails.application.repositories.TenantQueryRepository
import com.fstengineering.leasilycrm.stayDetails.infra.mappers.toReadModel

class TenantQueryRepositoryImpl(
    private val tenantDatabaseDataSource: TenantDatabaseDataSource,
) : TenantQueryRepository {
    override suspend fun getById(tenantId: Long): AppResult<TenantReadModel, ApplicationError> {
        return tenantDatabaseDataSource.getById(id = tenantId)
            .fold(
                onError = { AppResult.Error(error = it.toApplicationError()) },
                onSuccess = { AppResult.Success(data = it.toReadModel()) },
            )
    }
}
