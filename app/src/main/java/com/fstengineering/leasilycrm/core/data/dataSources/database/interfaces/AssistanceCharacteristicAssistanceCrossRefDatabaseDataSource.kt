package com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.AssistanceCharacteristicAssistanceCrossRefEntity

interface AssistanceCharacteristicAssistanceCrossRefDatabaseDataSource {
    suspend fun insertOrUpdate(
        crossRef: AssistanceCharacteristicAssistanceCrossRefEntity,
    )
}
