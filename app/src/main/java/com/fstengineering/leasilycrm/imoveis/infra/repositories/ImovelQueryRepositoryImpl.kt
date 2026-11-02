package com.fstengineering.leasilycrm.imoveis.infra.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.ImovelDatabaseDataSource
import com.fstengineering.leasilycrm.imoveis.application.models.dtos.ImovelReadModel
import com.fstengineering.leasilycrm.imoveis.application.repositories.ImovelQueryRepository
import com.fstengineering.leasilycrm.imoveis.infra.mappers.toReadModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class ImovelQueryRepositoryImpl(
    private val databaseDataSource: ImovelDatabaseDataSource,
) : ImovelQueryRepository {
    override suspend fun getAllMatching(
        matching: String?,
    ): AppResult<ImmutableList<ImovelReadModel>, ApplicationError> {
        return databaseDataSource.getAll(matching)
            .fold(
                onError = { AppResult.Error(error = it) },
                onSuccess = {
                    val imoveis =
                        it.map { imovel -> imovel.toReadModel() }.toPersistentList()
                    AppResult.Success(data = imoveis)
                },
            )
    }

    override fun watchAll(): Flow<AppResult<ImmutableList<ImovelReadModel>, ApplicationError>> =
        flow {
            val imoveisFlow = databaseDataSource.watchAll()
                .fold(
                    onError = { return@flow emit(AppResult.Error(error = it)) },
                    onSuccess = { it },
                )
                .map {
                    val imoveis =
                        it.map { imovel -> imovel.toReadModel() }.toPersistentList()
                    AppResult.Success<ImmutableList<ImovelReadModel>, ApplicationError>(data = imoveis)
                }
                .distinctUntilChanged()

            emitAll(imoveisFlow)
        }
}
