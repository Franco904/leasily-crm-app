package com.fstengineering.leasilycrm.core.application.services

import com.fstengineering.leasilycrm.core.application.services.interfaces.FileStorageService
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.infra.dataSources.sharedStorage.interfaces.SharedStorageDataSource

class FileStorageServiceImpl(
    private val sharedStorageDataSource: SharedStorageDataSource,
) : FileStorageService {
    override suspend fun getFileName(contentUriString: String): AppResult<String?, ApplicationError> {
        val fileName = sharedStorageDataSource.getFileName(contentUriString)

        return AppResult.Success(data = fileName)
    }
}
