package com.fstengineering.leasilycrm.core.data.repositories

import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.PropertyOwnerDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.PropertyOwnerFileDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyOwnerEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyOwnerFileEntity
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.encrypted.EncryptedPreferences
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.interfaces.PreferencesStorage
import com.fstengineering.leasilycrm.core.domain.models.PropertyOwner
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyOwnerRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class PropertyOwnerRepositoryImpl(
    private val propertyOwnerDao: PropertyOwnerDao,
    private val propertyOwnerFileDao: PropertyOwnerFileDao,
    private val encryptedPreferencesStorage: PreferencesStorage<EncryptedPreferences>,
) : PropertyOwnerRepository {
    override suspend fun insertOrUpdate(
        propertyOwner: PropertyOwner,
    ): AppResult<Unit, DataSourceError> {
        val userRemoteId = encryptedPreferencesStorage.readPreferences()
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { prefsFlow ->
                    prefsFlow.first().remoteId ?: return AppResult.Error(
                        error = DataSourceError.RemoteBackendError.NotFound,
                    )
                }
            )

        val propertyOwnerWithUser = propertyOwner.copy(userId = userRemoteId)
        val propertyOwnerEntity = PropertyOwnerEntity.fromDomain(propertyOwnerWithUser)

        // TODO: Convert all Int ids to Long in database
        val id = propertyOwnerDao.insertOrUpdate(propertyOwnerEntity).toInt()

        propertyOwner.files.forEach { file ->
            val fileEntity = PropertyOwnerFileEntity.fromDomain(
                domain = file.copy(propertyOwnerId = id),
            )
            propertyOwnerFileDao.insertOrUpdate(fileEntity)
        }

        return AppResult.Success(data = Unit)
    }

    override suspend fun delete(id: Int): AppResult<Unit, DataSourceError> {
        propertyOwnerDao.delete(id)

        return AppResult.Success(data = Unit)
    }

    override suspend fun getById(
        propertyOwnerId: Int,
    ): AppResult<PropertyOwner, DataSourceError> {
        val propertyOwner = propertyOwnerDao.getById(id = propertyOwnerId)
            .toDomain()

        return AppResult.Success(data = propertyOwner)
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
                propertyOwnerDao.watchAll(userId = userRemoteId)
                    .map {
                        val propertyOwners = it.map { it.toDomain() }.toPersistentList()
                        AppResult.Success<ImmutableList<PropertyOwner>, DataSourceError>(data = propertyOwners)
                    }
                    .distinctUntilChanged()
            )
        }
}
