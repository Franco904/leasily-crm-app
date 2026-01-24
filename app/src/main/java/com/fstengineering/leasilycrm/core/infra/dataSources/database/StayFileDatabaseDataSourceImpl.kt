package com.fstengineering.leasilycrm.core.infra.dataSources.database

import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.StayFileDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.StayFileEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.StayFileDatabaseDataSource

class StayFileDatabaseDataSourceImpl(
    private val dao: StayFileDao,
) : StayFileDatabaseDataSource {
    override suspend fun insertOrUpdate(
        stayFile: StayFileEntity,
    ) =
        dao.insertOrUpdate(stayFile)
}
