package com.fstengineering.leasilycrm.core.data.dataSources.database

import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.AssistanceInteractionDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.AssistanceInteractionEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces.AssistanceInteractionDatabaseDataSource
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError

class AssistanceInteractionDatabaseDataSourceImpl(
    private val dao: AssistanceInteractionDao,
) : AssistanceInteractionDatabaseDataSource {
    override suspend fun insertOrUpdate(
        assistanceInteraction: AssistanceInteractionEntity,
    ): AppResult<Unit, DataError> {
        return onDatabase { dao.insertOrUpdate(assistanceInteraction) }
    }
}
