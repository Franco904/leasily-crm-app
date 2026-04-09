package com.fstengineering.leasilycrm.stayRegistration.infra.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.TenantDatabaseDataSource
import com.fstengineering.leasilycrm.stayRegistration.application.models.dtos.TenantReadModel
import com.fstengineering.leasilycrm.stayRegistration.application.repositories.TenantQueryRepository
import com.fstengineering.leasilycrm.stayRegistration.infra.mappers.toReadModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

class TenantQueryRepositoryImpl(
    private val databaseDataSource: TenantDatabaseDataSource,
) : TenantQueryRepository {
    override suspend fun getById(tenantId: Long): AppResult<TenantReadModel, ApplicationError> {
        return databaseDataSource.getById(id = tenantId)
            .fold(
                onError = { AppResult.Error(error = it.toApplicationError()) },
                onSuccess = { AppResult.Success(data = it.toReadModel()) },
            )
    }

    override suspend fun getAllWithoutUpcomingStays(
        matching: String?,
        currentTenantId: Long?,
    ): AppResult<ImmutableList<TenantReadModel>, ApplicationError> {
        val tenants = databaseDataSource.getAllWithoutUpcomingStays(
            matching = matching,
            currentTenantId = currentTenantId,
        )
            .fold(
                onError = { return AppResult.Error(error = it.toApplicationError()) },
                onSuccess = { it },
            )
            .map { tenant -> tenant.toReadModel() }
            .toPersistentList()

        return AppResult.Success(data = tenants)
    }
}
