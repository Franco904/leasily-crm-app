package com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.AssistanceCharacteristicEntity
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError

interface AssistanceCharacteristicDatabaseDataSource {
    suspend fun insertOrUpdate(assistanceCharacteristic: AssistanceCharacteristicEntity)

    suspend fun getAll(): AppResult<List<AssistanceCharacteristicEntity>, DataError>
}
