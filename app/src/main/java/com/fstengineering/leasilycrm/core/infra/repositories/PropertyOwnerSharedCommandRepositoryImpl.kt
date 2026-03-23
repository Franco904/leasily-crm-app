package com.fstengineering.leasilycrm.core.infra.repositories

import com.fstengineering.leasilycrm.core.application.repositories.PropertyOwnerSharedCommandRepository
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.entities.PropertyOwner
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.infra.adapters.interfaces.DatabaseAdapter
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.PropertyOwnerDatabaseDataSource
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class PropertyOwnerSharedCommandRepositoryImpl(
    private val databaseAdapter: DatabaseAdapter,
    private val propertyOwnerDatabaseDataSource: PropertyOwnerDatabaseDataSource,
) : PropertyOwnerSharedCommandRepository {
    override suspend fun delete(id: Long): AppResult<Unit, ApplicationError> {
        return databaseAdapter.withTransaction {
            propertyOwnerDatabaseDataSource.delete(id)
        }
            .fold(
                onError = { AppResult.Error(error = it.toApplicationError()) },
                onSuccess = { AppResult.Success(data = Unit) }
            )
    }

    override fun watchById(
        propertyOwnerId: Long,
    ): Flow<AppResult<PropertyOwner, ApplicationError>> = flow {
        val propertyOwnerFlow = propertyOwnerDatabaseDataSource.watchById(id = propertyOwnerId)
            .fold(
                onError = { return@flow emit(AppResult.Error(error = it.toApplicationError())) },
                onSuccess = { it }
            )
            .map { propertyOwner ->
                val domain = propertyOwner.toDomain()

                AppResult.Success<PropertyOwner, ApplicationError>(data = domain)
            }
            .distinctUntilChanged()

        emitAll(propertyOwnerFlow)
    }

    override fun watchAll(): Flow<AppResult<ImmutableList<PropertyOwner>, ApplicationError>> =
        flow {
            emitAll(
                propertyOwnerDatabaseDataSource.watchAllCompound()
                    .fold(
                        onError = { return@flow emit(AppResult.Error(error = it.toApplicationError())) },
                        onSuccess = { it }
                    )
                    .map {
                        val propertyOwners = it.map { owner -> owner.toDomain() }.toPersistentList()
                        AppResult.Success<ImmutableList<PropertyOwner>, ApplicationError>(data = propertyOwners)
                    }
                    .distinctUntilChanged()
            )
        }
}
