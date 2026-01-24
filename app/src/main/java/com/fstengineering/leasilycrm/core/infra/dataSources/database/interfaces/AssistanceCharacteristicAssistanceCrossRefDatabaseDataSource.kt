package com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceCharacteristicAssistanceCrossRefEntity

interface AssistanceCharacteristicAssistanceCrossRefDatabaseDataSource {
    suspend fun insertOrUpdate(
        crossRef: AssistanceCharacteristicAssistanceCrossRefEntity,
    )
}
