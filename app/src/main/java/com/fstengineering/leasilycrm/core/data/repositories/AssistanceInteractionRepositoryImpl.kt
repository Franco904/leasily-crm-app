package com.fstengineering.leasilycrm.core.data.repositories

import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.AssistanceInteractionDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.AssistanceInteractionEntity
import com.fstengineering.leasilycrm.core.domain.models.AssistanceInteraction
import com.fstengineering.leasilycrm.core.domain.repositories.AssistanceInteractionRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError

class AssistanceInteractionRepositoryImpl(
    private val dao: AssistanceInteractionDao,
) : AssistanceInteractionRepository {
    override suspend fun insertOrUpdate(
        interaction: AssistanceInteraction,
    ): AppResult<Unit, DataError> {
        val interactionEntity = AssistanceInteractionEntity.fromDomain(domain = interaction)

        dao.insertOrUpdate(interactionEntity)

        return AppResult.Success(data = Unit)
    }
}
