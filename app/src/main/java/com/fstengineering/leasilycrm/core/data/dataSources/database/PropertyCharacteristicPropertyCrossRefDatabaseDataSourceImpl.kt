package com.fstengineering.leasilycrm.core.data.dataSources.database

import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.PropertyCharacteristicPropertyCrossRefDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyCharacteristicPropertyCrossRefEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces.PropertyCharacteristicPropertyCrossRefDatabaseDataSource
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError

class PropertyCharacteristicPropertyCrossRefDatabaseDataSourceImpl(
    private val dao: PropertyCharacteristicPropertyCrossRefDao,
) : PropertyCharacteristicPropertyCrossRefDatabaseDataSource {
    override suspend fun insertOrUpdate(
        crossRef: PropertyCharacteristicPropertyCrossRefEntity,
    ): AppResult<Unit, DataError> =
        onDatabase { dao.insertOrUpdate(crossRef) }
}
