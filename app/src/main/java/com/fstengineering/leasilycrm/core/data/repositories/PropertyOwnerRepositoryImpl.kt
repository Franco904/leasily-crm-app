package com.fstengineering.leasilycrm.core.data.repositories

import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyOwnerEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces.PropertyOwnerDatabaseDataSource
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.encrypted.EncryptedPreferences
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.interfaces.PreferencesDataSource
import com.fstengineering.leasilycrm.core.data.dataSources.sharedStorage.interfaces.SharedStorageDataSource
import com.fstengineering.leasilycrm.core.domain.models.PropertyOwner
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyOwnerRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
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
    private val databaseDataSource: PropertyOwnerDatabaseDataSource,
    private val encryptedPreferencesDataSource: PreferencesDataSource<EncryptedPreferences>,
    private val sharedStorageDataSource: SharedStorageDataSource,
) : PropertyOwnerRepository {
    override suspend fun insertOrUpdate(
        propertyOwner: PropertyOwner,
    ): AppResult<Long, DataError> {
        val userRemoteId = encryptedPreferencesDataSource.readPreferences()
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { prefsFlow -> prefsFlow.firstOrNull()?.remoteId ?: -1 }
            )

        val propertyOwnerWithUser = propertyOwner.copy(userId = userRemoteId)
        val propertyOwnerEntity = PropertyOwnerEntity.fromDomain(propertyOwnerWithUser)

        val id = databaseDataSource.insertOrUpdate(propertyOwnerEntity)
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { it },
            )
            .let { id -> propertyOwner.id ?: id }

        return AppResult.Success(data = id)
    }

    override suspend fun delete(id: Long): AppResult<Unit, DataError> {
        databaseDataSource.delete(id)
            .onError { return AppResult.Error(error = it) }

        return AppResult.Success(data = Unit)
    }

    override suspend fun getById(
        propertyOwnerId: Long,
    ): AppResult<PropertyOwner, DataError> {
        val propertyOwner = databaseDataSource.getById(id = propertyOwnerId)
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { it }
            )
            .toDomain()

        val filesWithNames = propertyOwner.files.map { file ->
            val fileName = sharedStorageDataSource.getFileName(contentUriString = file.uriString ?: "")

            file.copy(name = fileName)
        }.toImmutableList()

        return AppResult.Success(data = propertyOwner.copy(files = filesWithNames))
    }

    override fun watchById(
        propertyOwnerId: Long,
    ): Flow<AppResult<PropertyOwner, DataError>> = flow {
        val propertyOwnerFlow = databaseDataSource.watchById(id = propertyOwnerId)
            .fold(
                onError = { return@flow emit(AppResult.Error(error = it)) },
                onSuccess = { it }
            )
            .map { propertyOwner ->
                val domain = propertyOwner.toDomain()

                val filesWithNames = domain.files.map { file ->
                    val fileName =
                        sharedStorageDataSource.getFileName(contentUriString = file.uriString ?: "")

                    file.copy(name = fileName)
                }.toImmutableList()

                AppResult.Success<PropertyOwner, DataError>(
                    data = domain.copy(files = filesWithNames),
                )
            }
            .distinctUntilChanged()

        emitAll(propertyOwnerFlow)
    }

    override fun watchAll(): Flow<AppResult<ImmutableList<PropertyOwner>, DataError>> =
        flow {
            val userRemoteId = encryptedPreferencesDataSource.readPreferences()
                .fold(
                    onError = {
                        emit(AppResult.Error(error = it))
                        return@flow
                    },
                    onSuccess = { prefsFlow -> prefsFlow.firstOrNull()?.remoteId ?: -1 },
                )

            emitAll(
                databaseDataSource.watchAll(userId = userRemoteId)
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
        val userRemoteId = encryptedPreferencesDataSource.readPreferences()
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { prefsFlow -> prefsFlow.firstOrNull()?.remoteId ?: -1 },
            )

        val count = databaseDataSource.countPropertyOwnersWithName(
            userId = userRemoteId,
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
