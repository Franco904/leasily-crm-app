package com.fstengineering.leasilycrm.core.data.repositories

import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.PropertyOwnerDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyOwnerEntity
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.encrypted.EncryptedPreferences
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.interfaces.PreferencesStorage
import com.fstengineering.leasilycrm.core.data.dataSources.sharedStorage.interfaces.SharedStorage
import com.fstengineering.leasilycrm.core.domain.models.PropertyOwner
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyOwnerRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class PropertyOwnerRepositoryImpl(
    private val dao: PropertyOwnerDao,
    private val encryptedPreferencesStorage: PreferencesStorage<EncryptedPreferences>,
    private val sharedStorage: SharedStorage,
) : PropertyOwnerRepository {
    override suspend fun insertOrUpdate(
        propertyOwner: PropertyOwner,
    ): AppResult<Int, DataSourceError> {
        val userRemoteId = encryptedPreferencesStorage.readPreferences()
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { prefsFlow ->
                    prefsFlow.first().remoteId ?: return AppResult.Error(
                        error = DataSourceError.PreferencesError.NotFound,
                    )
                }
            )

        val propertyOwnerWithUser = propertyOwner.copy(userId = userRemoteId)
        val propertyOwnerEntity = PropertyOwnerEntity.fromDomain(propertyOwnerWithUser)

        // TODO: Convert all Int ids to Long in database
        val id = dao.insertOrUpdate(propertyOwnerEntity).toInt()

        return AppResult.Success(data = id)
    }

    override suspend fun delete(id: Int): AppResult<Unit, DataSourceError> {
        dao.delete(id)

        return AppResult.Success(data = Unit)
    }

    override suspend fun getById(
        propertyOwnerId: Int,
    ): AppResult<PropertyOwner, DataSourceError> {
        val propertyOwner = dao.getById(id = propertyOwnerId)
            .toDomain()

        val filesWithNames = propertyOwner.files.map { file ->
            val fileName = sharedStorage.getFileName(contentUriString = file.uriString ?: "")
                .fold(
                    onError = { error -> return AppResult.Error(error = error) },
                    onSuccess = { fileName -> fileName },
                )

            file.copy(name = fileName)
        }.toImmutableList()

        return AppResult.Success(data = propertyOwner.copy(files = filesWithNames))
    }

    override fun watchById(
        propertyOwnerId: Int,
    ): Flow<AppResult<PropertyOwner, DataSourceError>> = flow {
        val propertyOwnerFlow = dao.watchById(id = propertyOwnerId)
            .map { propertyOwner ->
                val domain = propertyOwner.toDomain()

                val filesWithNames = domain.files.map { file ->
                    val fileName =
                        sharedStorage.getFileName(contentUriString = file.uriString ?: "")
                            .fold(
                                onError = { error ->
                                    emit(AppResult.Error(error = error))
                                    return@map file
                                },
                                onSuccess = { fileName -> fileName },
                            )

                    file.copy(name = fileName)
                }.toImmutableList()

                AppResult.Success<PropertyOwner, DataSourceError>(
                    data = domain.copy(files = filesWithNames),
                )
            }
            .distinctUntilChanged()

        emitAll(propertyOwnerFlow)
    }

    override fun watchAll(): Flow<AppResult<ImmutableList<PropertyOwner>, DataSourceError>> =
        flow {
            val userRemoteId = encryptedPreferencesStorage.readPreferences()
                .fold(
                    onError = {
                        emit(AppResult.Error(error = it))
                        return@flow
                    },
                    onSuccess = { prefsFlow ->
                        prefsFlow.first().remoteId ?: run {
                            emit(AppResult.Error(error = DataSourceError.PreferencesError.NotFound))
                            return@flow
                        }
                    }
                )

            emitAll(
                dao.watchAll(userId = userRemoteId)
                    .map {
                        val propertyOwners = it.map { owner -> owner.toDomain() }.toPersistentList()
                        AppResult.Success<ImmutableList<PropertyOwner>, DataSourceError>(data = propertyOwners)
                    }
                    .distinctUntilChanged()
            )
        }
}
