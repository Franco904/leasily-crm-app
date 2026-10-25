package com.fstengineering.leasilycrm.propertyDashboard.infra.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.ProprietarioDatabaseDataSource
import com.fstengineering.leasilycrm.propertyDashboard.application.repositories.ProprietarioQueryRepository

class ProprietarioQueryRepositoryImpl(
    private val databaseDataSource: ProprietarioDatabaseDataSource,
) : ProprietarioQueryRepository {
    override suspend fun getNameById(
        proprietarioId: Long,
    ): AppResult<String, ApplicationError> {
        return databaseDataSource.getNameById(proprietarioId)
    }
}
