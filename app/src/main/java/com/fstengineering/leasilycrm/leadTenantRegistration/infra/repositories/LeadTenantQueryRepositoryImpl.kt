package com.fstengineering.leasilycrm.leadTenantRegistration.infra.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.TenantDatabaseDataSource
import com.fstengineering.leasilycrm.leadTenantRegistration.application.models.LeadTenantReadModel
import com.fstengineering.leasilycrm.leadTenantRegistration.application.repositories.LeadTenantQueryRepository
import com.fstengineering.leasilycrm.leadTenantRegistration.infra.mappers.toReadModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

class LeadTenantQueryRepositoryImpl(
    private val databaseDataSource: TenantDatabaseDataSource,
) : LeadTenantQueryRepository {
    override suspend fun getById(
        leadTenantId: Long,
    ): AppResult<LeadTenantReadModel, ApplicationError> {
        return databaseDataSource.getById(id = leadTenantId)
            .fold(
                onError = { AppResult.Error(error = it.toApplicationError()) },
                onSuccess = { AppResult.Success(data = it.toReadModel()) }
            )
    }

    override suspend fun getAllTagNames(): AppResult<ImmutableList<String>, ApplicationError> {
        return databaseDataSource.getAllTagNames()
            .fold(
                onError = { AppResult.Error(error = it.toApplicationError()) },
                onSuccess = { AppResult.Success(data = it.toPersistentList()) },
            )
    }

    override suspend fun getAllCityNames(): AppResult<ImmutableList<String>, ApplicationError> {
        return databaseDataSource.getAllCityNames()
            .fold(
                onError = { AppResult.Error(error = it.toApplicationError()) },
                onSuccess = { AppResult.Success(data = it.toPersistentList()) },
            )
    }

    override suspend fun getAllContactOrigins(): AppResult<ImmutableList<String>, ApplicationError> {
        return databaseDataSource.getAllContactOrigins()
            .fold(
                onError = { AppResult.Error(error = it.toApplicationError()) },
                onSuccess = { AppResult.Success(data = it.toPersistentList()) },
            )
    }
}
