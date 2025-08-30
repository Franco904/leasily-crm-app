package com.fstengineering.leasilycrm.core.domain.repositories

import com.fstengineering.leasilycrm.core.domain.models.PropertyOwnerFile
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError

interface PropertyOwnerFileRepository {
    suspend fun insertOrUpdate(file: PropertyOwnerFile): AppResult<Unit, DataSourceError>

    suspend fun getFileName(file: PropertyOwnerFile): AppResult<String, DataSourceError>
}
