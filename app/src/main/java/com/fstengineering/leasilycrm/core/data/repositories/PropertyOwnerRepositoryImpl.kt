package com.fstengineering.leasilycrm.core.data.repositories

import com.fstengineering.leasilycrm.core.infra.adapters.interfaces.DatabaseAdapter
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyOwnerEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyOwnerFileEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.PropertyOwnerDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.PropertyOwnerFileDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.encrypted.EncryptedPreferences
import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.interfaces.PreferencesDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.sharedStorage.interfaces.SharedStorageDataSource
import com.fstengineering.leasilycrm.core.domain.entities.PropertyOwner
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyOwnerRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.presentation.utils.normalizeEncoding
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class PropertyOwnerRepositoryImpl(
    private val databaseAdapter: DatabaseAdapter,
    private val propertyOwnerDatabaseDataSource: PropertyOwnerDatabaseDataSource,
    private val propertyOwnerFileDatabaseDataSource: PropertyOwnerFileDatabaseDataSource,
) : PropertyOwnerRepository {
    override suspend fun insertOrUpdate(
        propertyOwner: PropertyOwner,
    ): AppResult<Unit, DataError> {
        return databaseAdapter.withTransaction {
            saveDataInDatabase(propertyOwner)
        }
    }

    private suspend fun saveDataInDatabase(propertyOwner: PropertyOwner) {
        val propertyOwnerEntity = PropertyOwnerEntity.fromDomain(propertyOwner)

        val propertyOwnerId = propertyOwnerDatabaseDataSource.insertOrUpdate(propertyOwnerEntity)
            .let { id -> propertyOwner.id ?: id }

        propertyOwner.files.forEach {
            val file = PropertyOwnerFileEntity.fromDomain(it, propertyOwnerId)
            propertyOwnerFileDatabaseDataSource.insertOrUpdate(file)
        }
    }

    override suspend fun delete(id: Long): AppResult<Unit, DataError> =
        databaseAdapter.withTransaction {
            propertyOwnerDatabaseDataSource.delete(id)
        }

    override suspend fun getById(
        propertyOwnerId: Long,
    ): AppResult<PropertyOwner, DataError> {
        val propertyOwner = propertyOwnerDatabaseDataSource.getById(id = propertyOwnerId)
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { it }
            )
            .toDomain()

        return AppResult.Success(data = propertyOwner)
    }

    override fun watchById(
        propertyOwnerId: Long,
    ): Flow<AppResult<PropertyOwner, DataError>> = flow {
        val propertyOwnerFlow = propertyOwnerDatabaseDataSource.watchById(id = propertyOwnerId)
            .fold(
                onError = { return@flow emit(AppResult.Error(error = it)) },
                onSuccess = { it }
            )
            .map { propertyOwner ->
                val domain = propertyOwner.toDomain()

                AppResult.Success<PropertyOwner, DataError>(data = domain)
            }
            .distinctUntilChanged()

        emitAll(propertyOwnerFlow)
    }

    override fun watchAll(): Flow<AppResult<ImmutableList<PropertyOwner>, DataError>> =
        flow {
            emitAll(
                propertyOwnerDatabaseDataSource.watchAll()
                    .fold(
                        onError = { return@flow emit(AppResult.Error(error = it)) },
                        onSuccess = { it }
                    )
                    .map {
                        val propertyOwners = it.map { owner -> owner.toDomain() }.toPersistentList()
                        AppResult.Success<ImmutableList<PropertyOwner>, DataError>(data = propertyOwners)
                    }
                    .distinctUntilChanged()
            )
        }

    override suspend fun isTherePropertyOwnerWithName(
        propertyOwnerName: String,
        currentStoredPropertyOwnerName: String?,
    ): AppResult<Boolean, DataError> {
        val count = propertyOwnerDatabaseDataSource.countPropertyOwnersWithName(
            propertyOwnerName = propertyOwnerName.normalizeEncoding(),
            currentStoredPropertyOwnerName = currentStoredPropertyOwnerName,
        )
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { count -> count }
            )

        return AppResult.Success(data = count > 0)
    }
}
