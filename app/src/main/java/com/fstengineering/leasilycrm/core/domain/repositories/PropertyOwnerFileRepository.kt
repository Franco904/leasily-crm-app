package com.fstengineering.leasilycrm.core.domain.repositories

import com.fstengineering.leasilycrm.core.domain.models.PropertyOwnerFile
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError

interface PropertyOwnerFileRepository {
    suspend fun insertOrUpdate(file: PropertyOwnerFile): AppResult<Unit, DataError>

    suspend fun getFileName(file: PropertyOwnerFile): AppResult<String, DataError>
}
