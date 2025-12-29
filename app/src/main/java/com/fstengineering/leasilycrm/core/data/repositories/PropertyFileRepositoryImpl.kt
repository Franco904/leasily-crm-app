package com.fstengineering.leasilycrm.core.data.repositories

import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.PropertyFileDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyFileEntity
import com.fstengineering.leasilycrm.core.domain.models.PropertyFile
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyFileRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError

class PropertyFileRepositoryImpl(
    private val dao: PropertyFileDao,
) : PropertyFileRepository {
    override suspend fun insertOrUpdate(file: PropertyFile): AppResult<Unit, DataError> {
        val fileEntity = PropertyFileEntity.fromDomain(domain = file)
        dao.insertOrUpdate(fileEntity)

        return AppResult.Success(data = Unit)
    }
}
