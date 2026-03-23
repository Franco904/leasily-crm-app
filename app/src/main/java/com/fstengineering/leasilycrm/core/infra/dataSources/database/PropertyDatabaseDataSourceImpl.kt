package com.fstengineering.leasilycrm.core.infra.dataSources.database

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.PropertyDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.PropertyCompound
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.PropertyDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.utils.InfraError
import kotlinx.coroutines.flow.Flow

class PropertyDatabaseDataSourceImpl(
    private val dao: PropertyDao,
) : PropertyDatabaseDataSource {
    override suspend fun insertOrUpdate(property: PropertyEntity): Long =
        dao.insertOrUpdate(property)

    override suspend fun delete(id: Long) =
        dao.delete(id)

    override suspend fun getById(id: Long): AppResult<PropertyCompound, InfraError> {
        return onDatabaseSafe { dao.getById(id) }
    }

    override suspend fun getAll(matching: String?): AppResult<List<PropertyCompound>, InfraError> {
        return onDatabaseSafe { dao.getAll() }
    }

    override suspend fun getAllNeighborhoodNames(): AppResult<List<String>, InfraError> {
        return onDatabaseSafe { dao.getAllNeighborhoodNames() }
    }

    override suspend fun getAllSentInAssistance(
        assistanceId: Long,
    ): AppResult<List<PropertyCompound>, InfraError> {
        return onDatabaseSafe { dao.getAllSentInAssistance(assistanceId) }
    }

    override fun watchById(id: Long): AppResult<Flow<PropertyCompound?>, InfraError> {
        return onDatabaseSafe { dao.watchById(id) }
    }

    override fun watchAll(): AppResult<Flow<List<PropertyCompound>>, InfraError> {
        return onDatabaseSafe { dao.watchAll() }
    }
}
