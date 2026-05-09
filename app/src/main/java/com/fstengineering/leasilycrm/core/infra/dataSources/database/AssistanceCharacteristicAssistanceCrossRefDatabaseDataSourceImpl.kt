package com.fstengineering.leasilycrm.core.infra.dataSources.database

import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.AssistanceCharacteristicAssistanceCrossRefDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistancePropertyCharacteristicCrossRefEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.AssistanceCharacteristicAssistanceCrossRefDatabaseDataSource

class AssistanceCharacteristicAssistanceCrossRefDatabaseDataSourceImpl(
    private val dao: AssistanceCharacteristicAssistanceCrossRefDao,
) : AssistanceCharacteristicAssistanceCrossRefDatabaseDataSource {
    override suspend fun insertOrUpdate(
        crossRef: AssistancePropertyCharacteristicCrossRefEntity,
    ) =
        dao.insertOrUpdate(crossRef)
}
