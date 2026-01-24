package com.fstengineering.leasilycrm.core.data.dataSources.database

import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.PropertyDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.compounds.PropertyCompound
import com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces.PropertyDatabaseDataSource
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import kotlinx.coroutines.flow.Flow

class PropertyDatabaseDataSourceImpl(
    private val dao: PropertyDao,
) : PropertyDatabaseDataSource {
    override suspend fun insertOrUpdate(property: PropertyEntity): Long =
        dao.insertOrUpdate(property)

    override suspend fun delete(id: Long) =
        dao.delete(id)

    override suspend fun getById(id: Long): AppResult<PropertyCompound, DataError> {
        return onDatabaseSafe { dao.getById(id) }
    }

    override suspend fun getAll(userId: Long): AppResult<List<PropertyCompound>, DataError> {
        return onDatabaseSafe { dao.getAll(userId) }
    }

    override suspend fun getAllMatching(
        userId: Long,
        matching: String?
    ): AppResult<List<PropertyCompound>, DataError> {
        return onDatabaseSafe { dao.getAllMatching(userId, matching) }
    }

    override suspend fun getAllNeighborHoodNames(userId: Long): AppResult<List<String>, DataError> {
        return onDatabaseSafe { dao.getAllNeighborHoodNames(userId) }
    }

    override fun watchById(id: Long): AppResult<Flow<PropertyCompound?>, DataError> {
        return onDatabaseSafe { dao.watchById(id) }
    }

    override fun watchAll(userId: Long): AppResult<Flow<List<PropertyCompound>>, DataError> {
        return onDatabaseSafe { dao.watchAll(userId) }
    }
}


