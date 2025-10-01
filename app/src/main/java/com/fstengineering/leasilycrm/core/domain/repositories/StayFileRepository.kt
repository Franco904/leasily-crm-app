package com.fstengineering.leasilycrm.core.domain.repositories

import com.fstengineering.leasilycrm.core.domain.models.StayFile
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError

interface StayFileRepository {
    suspend fun insertOrUpdate(file: StayFile): AppResult<Unit, DataSourceError>

    suspend fun getFileName(file: StayFile): AppResult<String, DataSourceError>
}
