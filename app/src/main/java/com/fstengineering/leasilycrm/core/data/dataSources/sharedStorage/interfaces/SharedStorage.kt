package com.fstengineering.leasilycrm.core.data.dataSources.sharedStorage.interfaces

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError

interface SharedStorage {
    suspend fun getFileName(contentUriString: String): AppResult<String, DataSourceError>
}
