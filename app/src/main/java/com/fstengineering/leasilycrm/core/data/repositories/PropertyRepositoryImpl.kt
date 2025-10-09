package com.fstengineering.leasilycrm.core.data.repositories

import com.fstengineering.leasilycrm.core.application.models.LazyAppDate
import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.PropertyDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyEntity
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.encrypted.EncryptedPreferences
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.interfaces.PreferencesStorage
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.plain.PlainPreferences
import com.fstengineering.leasilycrm.core.domain.models.Property
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest

class PropertyRepositoryImpl(
    private val dao: PropertyDao,
    private val encryptedPreferencesStorage: PreferencesStorage<EncryptedPreferences>,
    private val plainPreferencesStorage: PreferencesStorage<PlainPreferences>,
    private val now: LazyAppDate,
) : PropertyRepository {
    override suspend fun insertOrUpdate(property: Property): AppResult<Long, DataSourceError> {
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

        val id = dao.insertOrUpdate(propertyEntity)
            .let { property.id ?: it }

        return AppResult.Success(data = id)
    }

    override suspend fun delete(id: Long): AppResult<Unit, DataSourceError> {
        dao.delete(id)

        return AppResult.Success(data = Unit)
    }

    override suspend fun getById(propertyId: Long): AppResult<Property, DataSourceError> {
        val property = dao.getById(id = propertyId)
            .toDomain()

        return AppResult.Success(data = property)
    }

    override suspend fun getAll(): AppResult<ImmutableList<Property>, DataSourceError> {
        val userRemoteId = encryptedPreferencesStorage.readPreferences()
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { prefsFlow ->
                    prefsFlow.first().remoteId ?: return AppResult.Error(
                        error = DataSourceError.PreferencesError.NotFound,
                    )
                }
            )

        val properties = dao.getAll(userId = userRemoteId)
            .map { property -> property.toDomain() }.toPersistentList()

        return AppResult.Success(data = properties)
    }

    override suspend fun getAllMatching(
        matching: String?,
    ): AppResult<ImmutableList<Property>, DataSourceError> {
        val userRemoteId = encryptedPreferencesStorage.readPreferences()
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { prefsFlow ->
                    prefsFlow.first().remoteId ?: return AppResult.Error(
                        error = DataSourceError.PreferencesError.NotFound,
                    )
                }
            )

        val properties = dao.getAllMatching(
            userId = userRemoteId,
            matching = matching,
        )
            .map { property -> property.toDomain() }.toPersistentList()

        return AppResult.Success(data = properties)
    }

    override suspend fun getAllNeighborHoodNames(): AppResult<ImmutableList<String>, DataSourceError> {
        val userRemoteId = encryptedPreferencesStorage.readPreferences()
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { prefsFlow ->
                    prefsFlow.first().remoteId ?: return AppResult.Error(
                        error = DataSourceError.PreferencesError.NotFound,
                    )
                }
            )

        val neighborHoodNames =
            dao.getAllNeighborHoodNames(userId = userRemoteId).toPersistentList()

        return AppResult.Success(data = neighborHoodNames)
    }

    override fun watchById(propertyId: Long): Flow<AppResult<Property?, DataSourceError>> = flow {
        val propertyFlow = dao.watchById(id = propertyId)
            .map { property ->
                AppResult.Success<Property?, DataSourceError>(data = property?.toDomain())
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

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun watchPropertyStaysSelectedYear(): Flow<AppResult<Int, DataSourceError>> {
        return plainPreferencesStorage.readPreferences()
            .fold(
                onError = { error -> flow { emit(AppResult.Error(error = error)) } },
                onSuccess = { prefsFlow ->
                    prefsFlow.mapLatest { prefs ->
                        val selectedYear = prefs.propertyStaysSelectedYear ?: now.date().year
                        AppResult.Success(data = selectedYear)
                    }
                }
            )
    }

    override suspend fun updatePropertyStaysSelectedYear(
        selectedYear: Int,
    ): AppResult<Unit, DataSourceError> {
        plainPreferencesStorage.savePreferences { prefs ->
            prefs.copy(propertyStaysSelectedYear = selectedYear)
        }
            .onError { return AppResult.Error(error = it) }

        return AppResult.Success(data = Unit)
    }
}
