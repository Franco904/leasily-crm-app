package com.fstengineering.leasilycrm.assistanceRegistration.infra.repositories

import com.fstengineering.leasilycrm.assistanceRegistration.application.models.AssistanceRawReadModel
import com.fstengineering.leasilycrm.assistanceRegistration.application.repositories.AssistanceQueryRepository
import com.fstengineering.leasilycrm.assistanceRegistration.infra.mappers.toRawReadModel
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.AssistanceDatabaseDataSource
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

class AssistanceQueryRepositoryImpl(
    private val assistanceDatabaseDataSource: AssistanceDatabaseDataSource,
) : AssistanceQueryRepository {
    override suspend fun getById(assistanceId: Long): AppResult<AssistanceRawReadModel, ApplicationError> {
        return assistanceDatabaseDataSource.getById(id = assistanceId)
            .fold(
                onError = { AppResult.Error(error = it.toApplicationError()) },
                onSuccess = { AppResult.Success(data = it.toRawReadModel()) }
            )
    }

    override suspend fun getAllNeighborhoodNames(): AppResult<ImmutableList<String>, ApplicationError> {
        val neighborhoodNames =
            assistanceDatabaseDataSource.getAllNeighborhoodNames()
                .fold(
                    onError = { return AppResult.Error(error = it.toApplicationError()) },
                    onSuccess = { neighborhoodNames -> neighborhoodNames.toPersistentList() }
                )

        return AppResult.Success(data = neighborhoodNames)
    }
}
