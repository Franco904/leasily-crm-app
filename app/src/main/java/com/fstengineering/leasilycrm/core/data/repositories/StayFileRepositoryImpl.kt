package com.fstengineering.leasilycrm.core.data.repositories

import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.StayFileDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.StayFileEntity
import com.fstengineering.leasilycrm.core.data.dataSources.sharedStorage.interfaces.SharedStorage
import com.fstengineering.leasilycrm.core.domain.models.StayFile
import com.fstengineering.leasilycrm.core.domain.repositories.StayFileRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError

class StayFileRepositoryImpl(
    private val dao: StayFileDao,
    private val sharedStorage: SharedStorage,
) : StayFileRepository {
    override suspend fun insertOrUpdate(file: StayFile): AppResult<Unit, DataError> {
        val fileEntity = StayFileEntity.fromDomain(domain = file)
        dao.insertOrUpdate(fileEntity)

        return AppResult.Success(data = Unit)
    }

    override suspend fun getFileName(file: StayFile): AppResult<String, DataError> {
        val fileName = sharedStorage.getFileName(contentUriString = file.uriString ?: "")

        return AppResult.Success(data = fileName)
    }
}
