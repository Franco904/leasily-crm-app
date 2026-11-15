package com.fstengineering.leasilycrm.core.infra.dataSources.database

import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.AssistanceSentImovelCrossRefDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceSentImovelCrossRefEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.AssistanceSentImovelCrossRefDatabaseDataSource

class AssistanceSentImovelCrossRefDatabaseDataSourceImpl(
    private val dao: AssistanceSentImovelCrossRefDao,
) : AssistanceSentImovelCrossRefDatabaseDataSource {
    override suspend fun insertOrUpdate(
        crossRef: AssistanceSentImovelCrossRefEntity,
    ) =
        dao.insertOrUpdate(crossRef)
}
