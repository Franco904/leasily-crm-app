package com.fstengineering.leasilycrm.core.data.dataSources.database

import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.AssistanceInteractionDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.AssistanceInteractionEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces.AssistanceInteractionDatabaseDataSource

class AssistanceInteractionDatabaseDataSourceImpl(
    private val dao: AssistanceInteractionDao,
) : AssistanceInteractionDatabaseDataSource {
    override suspend fun insertOrUpdate(
        interaction: AssistanceInteractionEntity,
    ) =
        dao.insertOrUpdate(interaction)
}
