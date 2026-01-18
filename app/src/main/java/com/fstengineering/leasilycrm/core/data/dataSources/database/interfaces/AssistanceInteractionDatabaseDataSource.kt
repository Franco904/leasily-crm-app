package com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.AssistanceInteractionEntity

interface AssistanceInteractionDatabaseDataSource {
    suspend fun insertOrUpdate(
        interaction: AssistanceInteractionEntity,
    )
}
