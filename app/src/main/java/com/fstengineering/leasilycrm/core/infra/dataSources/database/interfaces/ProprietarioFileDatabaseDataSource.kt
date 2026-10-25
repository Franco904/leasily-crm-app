package com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ProprietarioFileOrm

interface ProprietarioFileDatabaseDataSource {
    suspend fun insertOrUpdate(proprietarioFile: ProprietarioFileOrm)
}
