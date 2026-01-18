package com.fstengineering.leasilycrm.core.data.dataSources.database

import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.AssistanceCharacteristicAssistanceCrossRefDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.AssistanceCharacteristicAssistanceCrossRefEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces.AssistanceCharacteristicAssistanceCrossRefDatabaseDataSource
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError

class AssistanceCharacteristicAssistanceCrossRefDatabaseDataSourceImpl(
    private val dao: AssistanceCharacteristicAssistanceCrossRefDao,
) : AssistanceCharacteristicAssistanceCrossRefDatabaseDataSource {
    override suspend fun insertOrUpdate(
        crossRef: AssistanceCharacteristicAssistanceCrossRefEntity,
    ): AppResult<Unit, DataError> {
        return onDatabase { dao.insertOrUpdate(crossRef) }
    }
}
