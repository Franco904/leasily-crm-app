package com.fstengineering.leasilycrm.leadTenantDetails.infra.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.StayDatabaseDataSource
import com.fstengineering.leasilycrm.leadTenantDetails.application.models.dtos.StayReadModel
import com.fstengineering.leasilycrm.leadTenantDetails.application.repositories.StayQueryRepository
import com.fstengineering.leasilycrm.leadTenantDetails.infra.mappers.toReadModel

class StayQueryRepositoryImpl(
    private val stayDatabaseDataSource: StayDatabaseDataSource,
) : StayQueryRepository {
    override suspend fun getAllByTenantId(tenantId: Long): AppResult<List<StayReadModel>, ApplicationError> {
        return stayDatabaseDataSource.getAllByTenantId(tenantId)
            .fold(
                onError = { AppResult.Error(error = it) },
                onSuccess = { entities ->
                    AppResult.Success(data = entities.map { it.toReadModel() })
                },
            )
    }
}
