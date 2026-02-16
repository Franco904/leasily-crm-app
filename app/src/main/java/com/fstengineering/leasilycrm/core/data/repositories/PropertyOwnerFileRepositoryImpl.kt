package com.fstengineering.leasilycrm.core.data.repositories

import com.fstengineering.leasilycrm.core.infra.dataSources.sharedStorage.interfaces.SharedStorageDataSource
import com.fstengineering.leasilycrm.core.domain.entities.PropertyOwnerFile
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyOwnerFileRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError

class PropertyOwnerFileRepositoryImpl(
    private val sharedStorageDataSource: SharedStorageDataSource,
) : PropertyOwnerFileRepository {
    override suspend fun getFileName(file: PropertyOwnerFile): AppResult<String, DataError> {
        val fileName = sharedStorageDataSource.getFileName(contentUriString = file.uriString)

        return AppResult.Success(data = fileName)
    }
}
