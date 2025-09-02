package com.fstengineering.leasilycrm.core.data.repositories

import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.PropertyFileDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyFileEntity
import com.fstengineering.leasilycrm.core.data.dataSources.sharedStorage.interfaces.SharedStorage
import com.fstengineering.leasilycrm.core.domain.models.PropertyFile
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyFileRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError
import com.fstengineering.leasilycrm.core.domain.utils.fold

class PropertyFileRepositoryImpl(
    private val dao: PropertyFileDao,
    private val sharedStorage: SharedStorage,
) : PropertyFileRepository {
    override suspend fun insertOrUpdate(file: PropertyFile): AppResult<Unit, DataSourceError> {
        val fileEntity = PropertyFileEntity.fromDomain(domain = file)
        dao.insertOrUpdate(fileEntity)

        return AppResult.Success(data = Unit)
    }

    override suspend fun getFileName(file: PropertyFile): AppResult<String, DataSourceError> {
        val fileName = sharedStorage.getFileName(contentUriString = file.uriString ?: "")
            .fold(
                onError = { error -> return AppResult.Error(error = error) },
                onSuccess = { fileName -> fileName },
            )

        return AppResult.Success(data = fileName)
    }
}
