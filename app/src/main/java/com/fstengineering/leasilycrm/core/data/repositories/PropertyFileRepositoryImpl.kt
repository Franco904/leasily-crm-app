package com.fstengineering.leasilycrm.core.data.repositories

import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyFileEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces.PropertyFileDatabaseDataSource
import com.fstengineering.leasilycrm.core.domain.models.PropertyFile
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyFileRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import com.fstengineering.leasilycrm.core.domain.utils.onError

class PropertyFileRepositoryImpl(
    private val databaseDataSource: PropertyFileDatabaseDataSource,
) : PropertyFileRepository {
    override suspend fun insertOrUpdate(file: PropertyFile): AppResult<Unit, DataError> {
        val fileEntity = PropertyFileEntity.fromDomain(domain = file)

        databaseDataSource.insertOrUpdate(fileEntity)
            .onError { return AppResult.Error(error = it) }

        return AppResult.Success(data = Unit)
    }
}
