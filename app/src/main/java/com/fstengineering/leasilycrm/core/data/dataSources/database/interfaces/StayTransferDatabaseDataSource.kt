package com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.StayTransferEntity

interface StayTransferDatabaseDataSource {
    suspend fun insertOrUpdate(stayTransfer: StayTransferEntity)
}
