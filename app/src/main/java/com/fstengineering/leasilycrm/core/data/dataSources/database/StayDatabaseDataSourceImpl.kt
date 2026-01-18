package com.fstengineering.leasilycrm.core.data.dataSources.database

import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.StayDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.StayEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.compounds.StayCheckStatusCompound
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.compounds.StayCompound
import com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces.StayDatabaseDataSource
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import kotlinx.coroutines.flow.Flow

class StayDatabaseDataSourceImpl(
    private val dao: StayDao,
) : StayDatabaseDataSource {
    override suspend fun insertOrUpdate(stay: StayEntity): AppResult<Long, DataError> {
        return onDatabase { dao.insertOrUpdate(stay) }
    }

    override suspend fun delete(id: Long): AppResult<Unit, DataError> {
        return onDatabase { dao.delete(id) }
    }

    override suspend fun getById(id: Long): AppResult<StayCompound, DataError> {
        return onDatabase { dao.getById(id) }
    }

    override fun watchById(id: Long): AppResult<Flow<StayCompound?>, DataError> {
        return onDatabase { dao.watchById(id) }
    }

    override fun watchAll(userId: Long): AppResult<Flow<List<StayCompound>>, DataError> {
        return onDatabase { dao.watchAll(userId) }
    }

    override fun watchAllUpcomingCheckInsAndCheckOuts(userId: Long): AppResult<Flow<List<StayCheckStatusCompound>>, DataError> {
        return onDatabase { dao.watchAllUpcomingCheckInsAndCheckOuts(userId) }
    }
}
