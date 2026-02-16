package com.fstengineering.leasilycrm.core.infra.dataSources.database

import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.AssistanceDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.AssistanceCompound
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.AssistanceDatabaseDataSource
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError

class AssistanceDatabaseDataSourceImpl(
    private val dao: AssistanceDao,
) : AssistanceDatabaseDataSource {
    override suspend fun insertOrUpdate(assistance: AssistanceEntity): Long =
        dao.insertOrUpdate(assistance)

    override suspend fun delete(id: Long) =
        dao.delete(id)

    override suspend fun getById(id: Long): AppResult<AssistanceCompound, DataError> {
        return onDatabaseSafe { dao.getById(id) }
    }

    override suspend fun getAllNeighborhoodNames(): AppResult<List<String>, DataError> {
        return onDatabaseSafe { dao.getAllNeighborhoodNames() }
    }
}
