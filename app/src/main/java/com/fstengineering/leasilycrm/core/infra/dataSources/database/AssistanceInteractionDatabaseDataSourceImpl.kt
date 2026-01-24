package com.fstengineering.leasilycrm.core.infra.dataSources.database

import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.AssistanceInteractionDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceInteractionEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.AssistanceInteractionDatabaseDataSource

class AssistanceInteractionDatabaseDataSourceImpl(
    private val dao: AssistanceInteractionDao,
) : AssistanceInteractionDatabaseDataSource {
    override suspend fun insertOrUpdate(
        interaction: AssistanceInteractionEntity,
    ) =
        dao.insertOrUpdate(interaction)
}
