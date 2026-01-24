package com.fstengineering.leasilycrm.core.data.dataSources.database

import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.AssistanceSentPropertyCrossRefDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.AssistanceSentPropertyCrossRefEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces.AssistanceSentPropertyCrossRefDatabaseDataSource

class AssistanceSentPropertyCrossRefDatabaseDataSourceImpl(
    private val dao: AssistanceSentPropertyCrossRefDao,
) : AssistanceSentPropertyCrossRefDatabaseDataSource {
    override suspend fun insertOrUpdate(
        crossRef: AssistanceSentPropertyCrossRefEntity,
    ) =
        dao.insertOrUpdate(crossRef)
}
