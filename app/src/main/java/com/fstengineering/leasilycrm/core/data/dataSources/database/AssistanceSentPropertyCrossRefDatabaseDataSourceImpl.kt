package com.fstengineering.leasilycrm.core.data.dataSources.database

import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.AssistanceSentPropertyCrossRefDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.AssistanceSentPropertyCrossRefEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces.AssistanceSentPropertyCrossRefDatabaseDataSource
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError

class AssistanceSentPropertyCrossRefDatabaseDataSourceImpl(
    private val dao: AssistanceSentPropertyCrossRefDao,
) : AssistanceSentPropertyCrossRefDatabaseDataSource {
    override suspend fun insertOrUpdate(
        crossRef: AssistanceSentPropertyCrossRefEntity,
    ): AppResult<Unit, DataError> {
        return onDatabase { dao.insertOrUpdate(crossRef) }
    }
}
