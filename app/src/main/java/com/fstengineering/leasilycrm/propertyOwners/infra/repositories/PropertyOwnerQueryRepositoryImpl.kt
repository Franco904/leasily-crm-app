package com.fstengineering.leasilycrm.propertyOwners.infra.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.PropertyOwnerDatabaseDataSource
import com.fstengineering.leasilycrm.propertyOwners.application.models.dtos.PropertyOwnerReadModel
import com.fstengineering.leasilycrm.propertyOwners.application.repositories.PropertyOwnerQueryRepository
import com.fstengineering.leasilycrm.propertyOwners.infra.mappers.toReadModel
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
    override fun watchAll(): Flow<AppResult<ImmutableList<PropertyOwnerReadModel>, ApplicationError>> =
        flow {
            emitAll(
                databaseDataSource.watchAllCompound()
                    .fold(
                        onError = { return@flow emit(AppResult.Error(error = it.toApplicationError())) },
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
