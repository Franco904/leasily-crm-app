package com.fstengineering.leasilycrm.leadTenantDetails.infra.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.AssistanceDatabaseDataSource
import com.fstengineering.leasilycrm.leadTenantDetails.application.models.dtos.AssistanceRawReadModel
import com.fstengineering.leasilycrm.leadTenantDetails.application.repositories.AssistanceQueryRepository
import com.fstengineering.leasilycrm.leadTenantDetails.infra.mappers.toRawReadModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

class AssistanceQueryRepositoryImpl(
    private val assistanceDatabaseDataSource: AssistanceDatabaseDataSource,
) : AssistanceQueryRepository {
    override suspend fun getAllByLeadTenantId(
        leadTenantId: Long,
    ): AppResult<ImmutableList<AssistanceRawReadModel>, ApplicationError> {
        return assistanceDatabaseDataSource.getAllByLeadTenantId(leadTenantId)
            .fold(
                onError = { AppResult.Error(error = it) },
                onSuccess = { assistances ->
                    val readModels =
                        assistances.map { it.toRawReadModel() }.toPersistentList()
                    AppResult.Success(data = readModels)
                },
            )
    }
}
