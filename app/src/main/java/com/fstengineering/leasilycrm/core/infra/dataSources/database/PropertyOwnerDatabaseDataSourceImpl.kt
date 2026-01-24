package com.fstengineering.leasilycrm.core.infra.dataSources.database

import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.PropertyOwnerDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyOwnerEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.PropertyOwnerCompound
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.PropertyOwnerDatabaseDataSource
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import kotlinx.coroutines.flow.Flow

class PropertyOwnerDatabaseDataSourceImpl(
    private val dao: PropertyOwnerDao,
) : PropertyOwnerDatabaseDataSource {
    override suspend fun insertOrUpdate(propertyOwner: PropertyOwnerEntity): Long =
        dao.insertOrUpdate(propertyOwner)

    override suspend fun delete(id: Long) =
        dao.delete(id)

    override suspend fun getById(id: Long): AppResult<PropertyOwnerCompound, DataError> {
        return onDatabaseSafe { dao.getById(id) }
    }

    override fun watchById(id: Long): AppResult<Flow<PropertyOwnerCompound>, DataError> {
        return onDatabaseSafe { dao.watchById(id) }
    }

    override fun watchAll(userId: Long): AppResult<Flow<List<PropertyOwnerCompound>>, DataError> {
        return onDatabaseSafe { dao.watchAll(userId) }
    }

    override suspend fun countPropertyOwnersWithName(
        userId: Long,
        propertyOwnerName: String,
        currentStoredPropertyOwnerName: String?
    ): AppResult<Int, DataError> {
        return onDatabaseSafe {
            dao.countPropertyOwnersWithName(
                userId = userId,
                propertyOwnerName = propertyOwnerName,
                currentStoredPropertyOwnerName = currentStoredPropertyOwnerName,
            )
        }
    }
}
