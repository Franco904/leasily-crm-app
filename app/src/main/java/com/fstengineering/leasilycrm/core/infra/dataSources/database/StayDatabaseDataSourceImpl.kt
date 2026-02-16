package com.fstengineering.leasilycrm.core.infra.dataSources.database

import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.StayDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.StayEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.StayCheckStatusCompound
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.StayCompound
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.StayDatabaseDataSource
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import kotlinx.coroutines.flow.Flow

class StayDatabaseDataSourceImpl(
    private val dao: StayDao,
) : StayDatabaseDataSource {
    override suspend fun insertOrUpdate(stay: StayEntity): Long =
        dao.insertOrUpdate(stay)

    override suspend fun delete(id: Long) =
        dao.delete(id)

    override suspend fun getById(id: Long): AppResult<StayCompound, DataError> {
        return onDatabaseSafe { dao.getById(id) }
    }

    override fun watchById(id: Long): AppResult<Flow<StayCompound?>, DataError> {
        return onDatabaseSafe { dao.watchById(id) }
    }

    override fun watchAll(): AppResult<Flow<List<StayCompound>>, DataError> {
        return onDatabaseSafe { dao.watchAll() }
    }

    override fun watchAllUpcomingCheckInsAndCheckOuts(): AppResult<Flow<List<StayCheckStatusCompound>>, DataError> {
        return onDatabaseSafe { dao.watchAllUpcomingCheckInsAndCheckOuts() }
    }
}
