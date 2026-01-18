package com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.AssistanceCharacteristicAssistanceCrossRefEntity
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError

interface AssistanceCharacteristicAssistanceCrossRefDatabaseDataSource {
    suspend fun insertOrUpdate(
        crossRef: AssistanceCharacteristicAssistanceCrossRefEntity,
    ): AppResult<Unit, DataError>
}
