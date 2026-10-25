package com.fstengineering.leasilycrm.proprietarioRegistration.infra.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.ProprietarioDatabaseDataSource
import com.fstengineering.leasilycrm.proprietarioRegistration.application.models.dtos.ProprietarioReadModel
import com.fstengineering.leasilycrm.proprietarioRegistration.application.repositories.ProprietarioQueryRepository
import com.fstengineering.leasilycrm.proprietarioRegistration.infra.mappers.toReadModel

class ProprietarioQueryRepositoryImpl(
    private val databaseDataSource: ProprietarioDatabaseDataSource,
) : ProprietarioQueryRepository {
    override suspend fun getById(
        proprietarioId: Long,
    ): AppResult<ProprietarioReadModel, ApplicationError> {
        return databaseDataSource.getByIdCompound(id = proprietarioId)
            .fold(
                onError = { AppResult.Error(error = it) },
                onSuccess = { AppResult.Success(data = it.toReadModel()) }
            )
    }
}
