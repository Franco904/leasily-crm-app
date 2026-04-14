package com.fstengineering.leasilycrm.core.infra.repositories

import com.fstengineering.leasilycrm.core.application.repositories.StayFileRepository
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.infra.dataSources.sharedStorage.interfaces.SharedStorageDataSource
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.domain.entities.StayFile

class StayFileRepositoryImpl(
    private val sharedStorageDataSource: SharedStorageDataSource,
) : StayFileRepository {
    override suspend fun getFileName(file: StayFile): AppResult<String, ApplicationError> {
        val fileName = sharedStorageDataSource.getFileName(contentUriString = file.uriString)

        return AppResult.Success(data = fileName)
    }
}
