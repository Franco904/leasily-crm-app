package com.fstengineering.leasilycrm.core.domain.repositories

import com.fstengineering.leasilycrm.core.domain.models.StayTransfer
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError

interface StayTransferRepository {
    suspend fun insertOrUpdate(transfer: StayTransfer): AppResult<Unit, DataSourceError>
}
