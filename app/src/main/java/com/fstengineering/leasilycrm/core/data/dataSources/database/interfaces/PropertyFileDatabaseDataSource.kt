package com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyFileEntity
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError

interface PropertyFileDatabaseDataSource {
    suspend fun insertOrUpdate(propertyFile: PropertyFileEntity): AppResult<Unit, DataError>
}
