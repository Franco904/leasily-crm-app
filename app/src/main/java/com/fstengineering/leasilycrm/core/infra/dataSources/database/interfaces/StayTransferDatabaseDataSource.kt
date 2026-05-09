package com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.StayTransferEntity

interface StayTransferDatabaseDataSource {
    suspend fun insertOrUpdate(stayTransfer: StayTransferEntity)
}
