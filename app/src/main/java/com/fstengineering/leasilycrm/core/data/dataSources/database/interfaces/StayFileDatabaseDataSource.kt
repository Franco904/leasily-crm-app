package com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.StayFileEntity

interface StayFileDatabaseDataSource {
    suspend fun insertOrUpdate(stayFile: StayFileEntity)
}
