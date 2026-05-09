package com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceInteractionEntity

interface AssistanceInteractionDatabaseDataSource {
    suspend fun insertOrUpdate(
        interaction: AssistanceInteractionEntity,
    )
}
