package com.fstengineering.leasilycrm.core.infra.dataSources.database

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.PropertyDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.PropertyCompound
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.PropertyDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.utils.asDatabaseFlowResult
import com.fstengineering.leasilycrm.core.infra.utils.onDatabase
import kotlinx.coroutines.flow.Flow

class PropertyDatabaseDataSourceImpl(
    private val dao: PropertyDao,
) : PropertyDatabaseDataSource {
    override suspend fun insertOrUpdate(property: PropertyEntity): Long =
        dao.insertOrUpdate(property)

    override suspend fun delete(id: Long) =
        dao.delete(id)

    override suspend fun getById(id: Long): AppResult<PropertyCompound, ApplicationError> {
        return onDatabase { dao.getById(id) }
    }

    override suspend fun getAll(matching: String?): AppResult<List<PropertyCompound>, ApplicationError> {
        return onDatabase { dao.getAll() }
    }

    override suspend fun getAllNeighborhoodNames(): AppResult<List<String>, ApplicationError> {
        return onDatabase { dao.getAllNeighborhoodNames() }
    }

    override suspend fun getAllSentInAssistance(
        assistanceId: Long,
    ): AppResult<List<PropertyCompound>, ApplicationError> {
        return onDatabase { dao.getAllSentInAssistance(assistanceId) }
    }

    override fun watchById(id: Long): Flow<AppResult<PropertyCompound, ApplicationError>> {
        return dao.watchById(id).asDatabaseFlowResult()
    }

    override fun watchAll(): AppResult<Flow<List<PropertyCompound>>, ApplicationError> {
        return onDatabase { dao.watchAll() }
    }
}
