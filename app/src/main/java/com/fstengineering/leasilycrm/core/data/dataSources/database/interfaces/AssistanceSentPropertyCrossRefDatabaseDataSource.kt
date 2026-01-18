package com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.AssistanceSentPropertyCrossRefEntity
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError

interface AssistanceSentPropertyCrossRefDatabaseDataSource {
    suspend fun insertOrUpdate(
        crossRef: AssistanceSentPropertyCrossRefEntity,
    ): AppResult<Unit, DataError>
}
