package com.fstengineering.leasilycrm.core.data.repositories

import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyOwnerFileEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces.PropertyOwnerFileDatabaseDataSource
import com.fstengineering.leasilycrm.core.data.dataSources.sharedStorage.interfaces.SharedStorageDataSource
import com.fstengineering.leasilycrm.core.domain.models.PropertyOwnerFile
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyOwnerFileRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import com.fstengineering.leasilycrm.core.domain.utils.onError

class PropertyOwnerFileRepositoryImpl(
    private val databaseDataSource: PropertyOwnerFileDatabaseDataSource,
    private val sharedStorageDataSource: SharedStorageDataSource,
) : PropertyOwnerFileRepository {
    override suspend fun insertOrUpdate(file: PropertyOwnerFile): AppResult<Unit, DataError> {
        val fileEntity = PropertyOwnerFileEntity.fromDomain(domain = file)

        databaseDataSource.insertOrUpdate(fileEntity)
            .onError { return AppResult.Error(error = it) }

        return AppResult.Success(data = Unit)
    }

    override suspend fun getFileName(file: PropertyOwnerFile): AppResult<String, DataError> {
        val fileName = sharedStorageDataSource.getFileName(contentUriString = file.uriString ?: "")

        return AppResult.Success(data = fileName)
    }
}
