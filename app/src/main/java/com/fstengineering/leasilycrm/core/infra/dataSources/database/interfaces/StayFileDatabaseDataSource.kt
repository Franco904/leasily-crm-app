package com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.StayFileEntity

interface StayFileDatabaseDataSource {
    suspend fun insertOrUpdate(stayFile: StayFileEntity)
}
