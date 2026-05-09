package com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistancePropertyCharacteristicCrossRefEntity

interface AssistanceCharacteristicAssistanceCrossRefDatabaseDataSource {
    suspend fun insertOrUpdate(
        crossRef: AssistancePropertyCharacteristicCrossRefEntity,
    )
}
