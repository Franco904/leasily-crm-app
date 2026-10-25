package com.fstengineering.leasilycrm.core.infra.dataSources.database

import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.ProprietarioFileDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ProprietarioFileOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.ProprietarioFileDatabaseDataSource

class ProprietarioFileDatabaseDataSourceImpl(
    private val dao: ProprietarioFileDao,
) : ProprietarioFileDatabaseDataSource {
    override suspend fun insertOrUpdate(
        proprietarioFile: ProprietarioFileOrm,
    ) =
        dao.insertOrUpdate(proprietarioFile)
}
