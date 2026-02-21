package com.fstengineering.leasilycrm.core.infra.repositories

import com.fstengineering.leasilycrm.core.application.repositories.PropertyOwnerFileRepository
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.entities.PropertyOwnerFile
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.infra.dataSources.sharedStorage.interfaces.SharedStorageDataSource

class PropertyOwnerFileRepositoryImpl(
    private val sharedStorageDataSource: SharedStorageDataSource,
) : PropertyOwnerFileRepository {
    override suspend fun getFileName(file: PropertyOwnerFile): AppResult<String, ApplicationError> {
        val fileName = sharedStorageDataSource.getFileName(contentUriString = file.uriString)

        return AppResult.Success(data = fileName)
    }
}
