package com.fstengineering.leasilycrm.propertyRegistration.infra.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.PropertyOwnerDatabaseDataSource
import com.fstengineering.leasilycrm.propertyRegistration.application.models.dtos.PropertyOwnerReadModel
import com.fstengineering.leasilycrm.propertyRegistration.application.repositories.PropertyOwnerQueryRepository
import com.fstengineering.leasilycrm.propertyRegistration.infra.mappers.toReadModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class PropertyOwnerQueryRepositoryImpl(
    private val databaseDataSource: PropertyOwnerDatabaseDataSource,
) : PropertyOwnerQueryRepository {
    override suspend fun getById(
        propertyOwnerId: Long,
    ): AppResult<PropertyOwnerReadModel, ApplicationError> {
        return databaseDataSource.getNameById(id = propertyOwnerId)
            .fold(
                onError = { AppResult.Error(error = it) },
                onSuccess = {
                    val readModel = PropertyOwnerReadModel(
                        id = propertyOwnerId,
                        name = it,
                    )
                    AppResult.Success(data = readModel)
                }
            )
    }

    override fun watchAll(): Flow<AppResult<ImmutableList<PropertyOwnerReadModel>, ApplicationError>> =
        flow {
            emitAll(
                databaseDataSource.watchAll()
                    .fold(
                        onError = { return@flow emit(AppResult.Error(error = it)) },
                        onSuccess = { it }
                    )
                    .map {
                        val propertyOwners =
                            it.map { owner -> owner.toReadModel() }.toPersistentList()
                        AppResult.Success<ImmutableList<PropertyOwnerReadModel>, ApplicationError>(
                            data = propertyOwners
                        )
                    }
                    .distinctUntilChanged()
            )
        }
}
