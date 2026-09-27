package com.fstengineering.leasilycrm.core.infra.dataSources.database

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.PropertyOwnerDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyOwnerEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.PropertyOwnerCompound
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.PropertyOwnerDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.utils.onDatabase
import kotlinx.coroutines.flow.Flow

class PropertyOwnerDatabaseDataSourceImpl(
    private val dao: PropertyOwnerDao,
) : PropertyOwnerDatabaseDataSource {
    override suspend fun insertOrUpdate(propertyOwner: PropertyOwnerEntity): Long =
        dao.insertOrUpdate(propertyOwner)

    override suspend fun delete(id: Long) =
        dao.delete(id)

    override suspend fun getNameById(id: Long): AppResult<String, ApplicationError> {
        return onDatabase { dao.getNameById(id) }
    }

    override suspend fun getByIdCompound(id: Long): AppResult<PropertyOwnerCompound, ApplicationError> {
        return onDatabase { dao.getByIdCompound(id) }
    }

    override fun watchById(id: Long): AppResult<Flow<PropertyOwnerCompound>, ApplicationError> {
        return onDatabase { dao.watchById(id) }
    }

    override fun watchAll(): AppResult<Flow<List<PropertyOwnerEntity>>, ApplicationError> {
        return onDatabase { dao.watchAll() }
    }

    override fun watchAllCompound(): AppResult<Flow<List<PropertyOwnerCompound>>, ApplicationError> {
        return onDatabase { dao.watchAllCompound() }
    }
}
