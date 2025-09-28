package com.fstengineering.leasilycrm.core.data.repositories

import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.StayFileDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.StayFileEntity
import com.fstengineering.leasilycrm.core.data.dataSources.sharedStorage.interfaces.SharedStorage
import com.fstengineering.leasilycrm.core.domain.models.PropertyOwnerFile
import com.fstengineering.leasilycrm.core.domain.models.StayFile
import com.fstengineering.leasilycrm.core.domain.repositories.StayFileRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError
import com.fstengineering.leasilycrm.core.domain.utils.fold

class StayFileRepositoryImpl(
    private val dao: StayFileDao,
    private val sharedStorage: SharedStorage,
) : StayFileRepository {
    override suspend fun insertOrUpdate(file: StayFile): AppResult<Unit, DataSourceError> {
        val fileEntity = StayFileEntity.fromDomain(domain = file)
        dao.insertOrUpdate(fileEntity)

        return AppResult.Success(data = Unit)
    }

    override suspend fun getFileName(file: StayFile): AppResult<String, DataSourceError> {
        val fileName = sharedStorage.getFileName(contentUriString = file.uriString ?: "")
            .fold(
                onError = { error -> return AppResult.Error(error = error) },
                onSuccess = { fileName -> fileName },
            )

        return AppResult.Success(data = fileName)
    }
}
