package com.fstengineering.leasilycrm.core.data.repositories

import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.AssistanceInteractionEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces.AssistanceInteractionDatabaseDataSource
import com.fstengineering.leasilycrm.core.domain.models.AssistanceInteraction
import com.fstengineering.leasilycrm.core.domain.repositories.AssistanceInteractionRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import com.fstengineering.leasilycrm.core.domain.utils.onError

class AssistanceInteractionRepositoryImpl(
    private val databaseDataSource: AssistanceInteractionDatabaseDataSource,
) : AssistanceInteractionRepository {
    override suspend fun insertOrUpdate(
        interaction: AssistanceInteraction,
    ): AppResult<Unit, DataError> {
        val interactionEntity = AssistanceInteractionEntity.fromDomain(domain = interaction)

        databaseDataSource.insertOrUpdate(interactionEntity)
            .onError { return AppResult.Error(error = it) }

        return AppResult.Success(data = Unit)
    }
}
