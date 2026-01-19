package com.fstengineering.leasilycrm.core.data.dataSources.database

import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.StayFileDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.StayFileEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces.StayFileDatabaseDataSource

class StayFileDatabaseDataSourceImpl(
    private val dao: StayFileDao,
) : StayFileDatabaseDataSource {
    override suspend fun insertOrUpdate(
        stayFile: StayFileEntity,
    ) =
        dao.insertOrUpdate(stayFile)
}
