package com.fstengineering.leasilycrm.properties.infra.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.PropertyDatabaseDataSource
import com.fstengineering.leasilycrm.properties.application.models.dtos.PropertyReadModel
import com.fstengineering.leasilycrm.properties.application.repositories.PropertyQueryRepository
import com.fstengineering.leasilycrm.properties.infra.mappers.toReadModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class PropertyQueryRepositoryImpl(
    private val databaseDataSource: PropertyDatabaseDataSource,
) : PropertyQueryRepository {
    override suspend fun getAllMatching(
        matching: String?,
    ): AppResult<ImmutableList<PropertyReadModel>, ApplicationError> {
        return databaseDataSource.getAll(matching)
            .fold(
                onError = { AppResult.Error(error = it.toApplicationError()) },
                onSuccess = {
                    val properties =
                        it.map { property -> property.toReadModel() }.toPersistentList()
                    AppResult.Success(data = properties)
                },
            )
    }

    override fun watchAll(): Flow<AppResult<ImmutableList<PropertyReadModel>, ApplicationError>> =
        flow {
            val propertiesFlow = databaseDataSource.watchAll()
                .fold(
                    onError = { return@flow emit(AppResult.Error(error = it.toApplicationError())) },
                    onSuccess = { it },
                )
                .map {
                    val properties =
                        it.map { property -> property.toReadModel() }.toPersistentList()
                    AppResult.Success<ImmutableList<PropertyReadModel>, ApplicationError>(data = properties)
                }
                .distinctUntilChanged()

            emitAll(propertiesFlow)
        }
}
