package com.fstengineering.leasilycrm.proprietarioDetails.infra.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.ProprietarioDatabaseDataSource
import com.fstengineering.leasilycrm.proprietarioDetails.application.models.ProprietarioReadModel
import com.fstengineering.leasilycrm.proprietarioDetails.application.repositories.ProprietarioQueryRepository
import com.fstengineering.leasilycrm.proprietarioDetails.infra.mappers.toReadModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class ProprietarioQueryRepositoryImpl(
    private val databaseDataSource: ProprietarioDatabaseDataSource,
) : ProprietarioQueryRepository {
    override fun watchById(
        proprietarioId: Long,
    ): Flow<AppResult<ProprietarioReadModel, ApplicationError>> = flow {
        val proprietarioFlow = databaseDataSource.watchById(id = proprietarioId)
            .fold(
                onError = { return@flow emit(AppResult.Error(error = it)) },
                onSuccess = { it }
            )
            .map { AppResult.Success<ProprietarioReadModel, ApplicationError>(data = it.toReadModel()) }
            .distinctUntilChanged()

        emitAll(proprietarioFlow)
    }
}
