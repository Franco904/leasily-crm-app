package com.fstengineering.leasilycrm.core.application.repositories

import com.fstengineering.leasilycrm.core.domain.entities.PropertyOwnerFile
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError

interface PropertyOwnerFileRepository {
    suspend fun getFileName(file: PropertyOwnerFile): AppResult<String, DataError>
}
