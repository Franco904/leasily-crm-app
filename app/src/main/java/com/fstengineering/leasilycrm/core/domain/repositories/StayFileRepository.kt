package com.fstengineering.leasilycrm.core.domain.repositories

import com.fstengineering.leasilycrm.core.domain.entities.StayFile
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError

interface StayFileRepository {
    suspend fun getFileName(file: StayFile): AppResult<String, DataError>
}
