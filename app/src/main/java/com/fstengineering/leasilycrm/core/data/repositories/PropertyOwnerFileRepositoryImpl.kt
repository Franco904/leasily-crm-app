package com.fstengineering.leasilycrm.core.data.repositories

import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.PropertyOwnerFileDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyOwnerFileEntity
import com.fstengineering.leasilycrm.core.data.dataSources.sharedStorage.interfaces.SharedStorage
import com.fstengineering.leasilycrm.core.domain.models.PropertyOwnerFile
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyOwnerFileRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError
import com.fstengineering.leasilycrm.core.domain.utils.fold

class PropertyOwnerFileRepositoryImpl(
    private val propertyOwnerFileDao: PropertyOwnerFileDao,
    private val sharedStorage: SharedStorage,
) : PropertyOwnerFileRepository {
    override suspend fun insertOrUpdate(file: PropertyOwnerFile): AppResult<Unit, DataSourceError> {
        val fileEntity = PropertyOwnerFileEntity.fromDomain(domain = file)
        propertyOwnerFileDao.insertOrUpdate(fileEntity)

        return AppResult.Success(data = Unit)
    }

    override suspend fun getFileName(file: PropertyOwnerFile): AppResult<String, DataSourceError> {
        val fileName = sharedStorage.getFileName(contentUriString = file.uriString ?: "")
            .fold(
                onError = { error -> return AppResult.Error(error = error) },
                onSuccess = { fileName -> fileName },
            )

        return AppResult.Success(data = fileName)
    }
}
