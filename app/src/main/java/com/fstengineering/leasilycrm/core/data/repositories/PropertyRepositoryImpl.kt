package com.fstengineering.leasilycrm.core.data.repositories

import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.PropertyDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyEntity
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.encrypted.EncryptedPreferences
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.interfaces.PreferencesStorage
import com.fstengineering.leasilycrm.core.data.dataSources.sharedStorage.interfaces.SharedStorage
import com.fstengineering.leasilycrm.core.domain.models.Property
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyRepository
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

class PropertyRepositoryImpl(
    private val dao: PropertyDao,
    private val encryptedPreferencesStorage: PreferencesStorage<EncryptedPreferences>,
    private val sharedStorage: SharedStorage,
) : PropertyRepository {
    override suspend fun insertOrUpdate(property: Property): AppResult<Int, DataSourceError> {
        val userRemoteId = encryptedPreferencesStorage.readPreferences()
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { prefsFlow ->
                    prefsFlow.first().remoteId ?: return AppResult.Error(
                        error = DataSourceError.PreferencesError.NotFound,
                    )
                }
            )

        val propertyWithUser = property.copy(userId = userRemoteId)
        val propertyEntity = PropertyEntity.fromDomain(propertyWithUser)

        // TODO: Convert all Int ids to Long in database
        val id = dao.insertOrUpdate(propertyEntity).toInt()

        return AppResult.Success(data = id)
    }

    override suspend fun delete(id: Int): AppResult<Unit, DataSourceError> {
        dao.delete(id)

        return AppResult.Success(data = Unit)
    }

    override suspend fun getById(propertyId: Int): AppResult<Property, DataSourceError> {
        val property = dao.getById(id = propertyId)
            .toDomain()

        val filesWithNames = property.files.map { file ->
            val fileName = sharedStorage.getFileName(contentUriString = file.uriString ?: "")
                .fold(
                    onError = { error -> return AppResult.Error(error = error) },
                    onSuccess = { fileName -> fileName },
                )

            file.copy(name = fileName)
        }.toImmutableList()

        return AppResult.Success(data = property.copy(files = filesWithNames))
    }

    override fun watchById(propertyId: Int): Flow<AppResult<Property, DataSourceError>> = flow {
        val propertyFlow = dao.watchById(id = propertyId)
            .map { property ->
                val domain = property.toDomain()

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

                AppResult.Success<Property, DataSourceError>(
                    data = domain.copy(files = filesWithNames),
                )
            }
            .distinctUntilChanged()

        emitAll(propertyFlow)
    }

    override fun watchAll(): Flow<AppResult<ImmutableList<Property>, DataSourceError>> =
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

            val propertiesFlow = dao.watchAll(userId = userRemoteId)
                .map {
                    val properties = it.map { property -> property.toDomain() }.toPersistentList()
                    AppResult.Success<ImmutableList<Property>, DataSourceError>(data = properties)
                }
                .distinctUntilChanged()

            emitAll(propertiesFlow)
        }
}
