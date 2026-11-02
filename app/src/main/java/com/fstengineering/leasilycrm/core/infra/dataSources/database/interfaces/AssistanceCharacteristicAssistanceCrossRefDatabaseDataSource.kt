package com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceImovelCharacteristicCrossRefEntity

interface AssistanceCharacteristicAssistanceCrossRefDatabaseDataSource {
    suspend fun insertOrUpdate(
        crossRef: AssistanceImovelCharacteristicCrossRefEntity,
    )
}
