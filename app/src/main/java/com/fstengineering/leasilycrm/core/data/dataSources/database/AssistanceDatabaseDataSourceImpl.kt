package com.fstengineering.leasilycrm.core.data.dataSources.database

import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.AssistanceDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.AssistanceEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.compounds.AssistanceCompound
import com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces.AssistanceDatabaseDataSource
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError

class AssistanceDatabaseDataSourceImpl(
    private val dao: AssistanceDao,
) : AssistanceDatabaseDataSource {
    override suspend fun insertOrUpdate(assistance: AssistanceEntity): AppResult<Long, DataError> {
        return onDatabase { dao.insertOrUpdate(assistance) }
    }

    override suspend fun delete(id: Long): AppResult<Unit, DataError> {
        return onDatabase { dao.delete(id) }
    }

    override suspend fun getById(id: Long): AppResult<AssistanceCompound, DataError> {
        return onDatabase { dao.getById(id) }
    }

    override suspend fun getAllNeighborhoodNames(userId: Long): AppResult<List<String>, DataError> {
        return onDatabase { dao.getAllNeighborhoodNames(userId) }
    }
}
