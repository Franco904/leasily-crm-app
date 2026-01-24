package com.fstengineering.leasilycrm.core.infra.dataSources.database

import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.AssistanceCharacteristicDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceCharacteristicEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.AssistanceCharacteristicDatabaseDataSource
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError

class AssistanceCharacteristicDatabaseDataSourceImpl(
    private val dao: AssistanceCharacteristicDao,
) : AssistanceCharacteristicDatabaseDataSource {
    override suspend fun insertOrUpdate(
        assistanceCharacteristic: AssistanceCharacteristicEntity,
    ) =
        dao.insertOrUpdate(assistanceCharacteristic)

    override suspend fun getAll(): AppResult<List<AssistanceCharacteristicEntity>, DataError> {
        return onDatabaseSafe { dao.getAll() }
    }
}
