package com.fstengineering.leasilycrm.proprietarios.infra.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.ProprietarioDatabaseDataSource
import com.fstengineering.leasilycrm.proprietarios.application.models.dtos.ProprietarioReadModel
import com.fstengineering.leasilycrm.proprietarios.application.repositories.ProprietarioQueryRepository
import com.fstengineering.leasilycrm.proprietarios.infra.mappers.toReadModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class ProprietarioQueryRepositoryImpl(
    private val databaseDataSource: ProprietarioDatabaseDataSource,
) : ProprietarioQueryRepository {
    override fun watchAll(): Flow<AppResult<ImmutableList<ProprietarioReadModel>, ApplicationError>> =
        flow {
            emitAll(
                databaseDataSource.watchAllCompound()
                    .fold(
                        onError = { return@flow emit(AppResult.Error(error = it)) },
                        onSuccess = { it }
                    )
                    .map {
                        val proprietarios =
                            it.map { owner -> owner.toReadModel() }.toPersistentList()
                        AppResult.Success<ImmutableList<ProprietarioReadModel>, ApplicationError>(
                            data = proprietarios
                        )
                    }
                    .distinctUntilChanged()
            )
        }
}
