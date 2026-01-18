package com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.AssistanceInteractionEntity
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError

interface AssistanceInteractionDatabaseDataSource {
    suspend fun insertOrUpdate(
        assistanceInteraction: AssistanceInteractionEntity,
    ): AppResult<Unit, DataError>
}
