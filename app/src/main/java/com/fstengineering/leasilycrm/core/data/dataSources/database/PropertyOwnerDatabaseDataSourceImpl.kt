package com.fstengineering.leasilycrm.core.data.dataSources.database

import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.PropertyOwnerDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyOwnerEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.compounds.PropertyOwnerCompound
import com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces.PropertyOwnerDatabaseDataSource
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import kotlinx.coroutines.flow.Flow

class PropertyOwnerDatabaseDataSourceImpl(
    private val dao: PropertyOwnerDao,
) : PropertyOwnerDatabaseDataSource {
    override suspend fun insertOrUpdate(propertyOwner: PropertyOwnerEntity): AppResult<Long, DataError> {
        return onDatabase { dao.insertOrUpdate(propertyOwner) }
    }

    override suspend fun delete(id: Long): AppResult<Unit, DataError> {
        return onDatabase { dao.delete(id) }
    }

    override suspend fun getById(id: Long): AppResult<PropertyOwnerCompound, DataError> {
        return onDatabase { dao.getById(id) }
    }

    override fun watchById(id: Long): AppResult<Flow<PropertyOwnerCompound>, DataError> {
        return onDatabase { dao.watchById(id) }
    }

    override fun watchAll(userId: Long): AppResult<Flow<List<PropertyOwnerCompound>>, DataError> {
        return onDatabase { dao.watchAll(userId) }
    }

    override suspend fun countPropertyOwnersWithName(
        userId: Long,
        propertyOwnerName: String,
        currentStoredPropertyOwnerName: String?
    ): AppResult<Int, DataError> {
        return onDatabase {
            dao.countPropertyOwnersWithName(
                userId = userId,
                propertyOwnerName = propertyOwnerName,
                currentStoredPropertyOwnerName = currentStoredPropertyOwnerName,
            )
        }
    }
}
