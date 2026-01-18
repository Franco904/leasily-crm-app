package com.fstengineering.leasilycrm.core.data.repositories

import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces.PropertyDatabaseDataSource
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.encrypted.EncryptedPreferences
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.interfaces.PreferencesDataSource
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.plain.PlainPreferences
import com.fstengineering.leasilycrm.core.domain.models.CalendarMonthYear
import com.fstengineering.leasilycrm.core.domain.models.Property
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest

class PropertyRepositoryImpl(
    private val databaseDataSource: PropertyDatabaseDataSource,
    private val encryptedPreferencesDataSource: PreferencesDataSource<EncryptedPreferences>,
    private val plainPreferencesDataSource: PreferencesDataSource<PlainPreferences>,
) : PropertyRepository {
    override suspend fun insertOrUpdate(property: Property): AppResult<Long, DataError> {
        val userRemoteId = encryptedPreferencesDataSource.readPreferences()
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { prefsFlow -> prefsFlow.firstOrNull()?.remoteId ?: -1 },
            )

        val propertyWithUser = property.copy(userId = userRemoteId)
        val propertyEntity = PropertyEntity.fromDomain(propertyWithUser)

        val id = databaseDataSource.insertOrUpdate(propertyEntity)
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { it },
            )
            .let { property.id ?: it }

        return AppResult.Success(data = id)
    }

    override suspend fun delete(id: Long): AppResult<Unit, DataError> {
        databaseDataSource.delete(id)
            .onError { return AppResult.Error(error = it) }

        return AppResult.Success(data = Unit)
    }

    override suspend fun getById(propertyId: Long): AppResult<Property, DataError> {
        val property = databaseDataSource.getById(id = propertyId)
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { it },
            )
            .toDomain()

        return AppResult.Success(data = property)
    }

    override suspend fun getAll(): AppResult<ImmutableList<Property>, DataError> {
        val userRemoteId = encryptedPreferencesDataSource.readPreferences()
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { prefsFlow -> prefsFlow.firstOrNull()?.remoteId ?: -1 },
            )

        val properties = databaseDataSource.getAll(userId = userRemoteId)
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { it },
            )
            .map { property -> property.toDomain() }.toPersistentList()

        return AppResult.Success(data = properties)
    }

    override suspend fun getAllMatching(
        matching: String?,
    ): AppResult<ImmutableList<Property>, DataError> {
        val userRemoteId = encryptedPreferencesDataSource.readPreferences()
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { prefsFlow -> prefsFlow.firstOrNull()?.remoteId ?: -1 },
            )

        val properties = databaseDataSource.getAllMatching(
            userId = userRemoteId,
            matching = matching,
        )
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { it },
            )
            .map { property -> property.toDomain() }.toPersistentList()

        return AppResult.Success(data = properties)
    }

    override suspend fun getAllNeighborHoodNames(): AppResult<ImmutableList<String>, DataError> {
        val userRemoteId = encryptedPreferencesDataSource.readPreferences()
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { prefsFlow -> prefsFlow.firstOrNull()?.remoteId ?: -1 },
            )

        val neighborHoodNames = databaseDataSource.getAllNeighborHoodNames(userId = userRemoteId)
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { it },
            )
            .toPersistentList()

        return AppResult.Success(data = neighborHoodNames)
    }

    override fun watchById(propertyId: Long): Flow<AppResult<Property?, DataError>> = flow {
        val propertyFlow = databaseDataSource.watchById(id = propertyId)
            .fold(
                onError = { return@flow emit(AppResult.Error(error = it)) },
                onSuccess = { it },
            )
            .map { property ->
                AppResult.Success<Property?, DataError>(data = property?.toDomain())
            }
            .distinctUntilChanged()

        emitAll(propertyFlow)
    }

    override fun watchAll(): Flow<AppResult<ImmutableList<Property>, DataError>> =
        flow {
            val userRemoteId = encryptedPreferencesDataSource.readPreferences()
                .fold(
                    onError = {
                        emit(AppResult.Error(error = it))
                        return@flow
                    },
                    onSuccess = { prefsFlow -> prefsFlow.firstOrNull()?.remoteId ?: -1 },
                )

            val propertiesFlow = databaseDataSource.watchAll(userId = userRemoteId)
                .fold(
                    onError = { return@flow emit(AppResult.Error(error = it)) },
                    onSuccess = { it },
                )
                .map {
                    val properties = it.map { property -> property.toDomain() }.toPersistentList()
                    AppResult.Success<ImmutableList<Property>, DataError>(data = properties)
                }
                .distinctUntilChanged()

            emitAll(propertiesFlow)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun watchPropertyStaysSelectedPeriod(): Flow<AppResult<Pair<CalendarMonthYear?, CalendarMonthYear?>, DataError>> {
        return plainPreferencesDataSource.readPreferences()
            .fold(
                onError = { error -> flow { emit(AppResult.Error(error = error)) } },
                onSuccess = { prefsFlow ->
                    prefsFlow.mapLatest { prefs ->
                        val startMonth = prefs.propertyStaysSelectedPeriodStartMonth
                        val startYear = prefs.propertyStaysSelectedPeriodStartYear
                        val calendarStart = if (startMonth != null && startYear != null) {
                            CalendarMonthYear(
                                month = startMonth,
                                year = startYear,
                            )
                        } else null

                        val endMonth = prefs.propertyStaysSelectedPeriodEndMonth
                        val endYear = prefs.propertyStaysSelectedPeriodEndYear
                        val calendarEnd = if (endMonth != null && endYear != null) {
                            CalendarMonthYear(
                                month = endMonth,
                                year = endYear,
                            )
                        } else null

                        AppResult.Success(data = calendarStart to calendarEnd)
                    }
                }
            )
    }

    override suspend fun updatePropertyStaysSelectedPeriod(
        selectedStartMonth: Int?,
        selectedStartYear: Int?,
        selectedEndMonth: Int?,
        selectedEndYear: Int?,
    ): AppResult<Unit, DataError> {
        plainPreferencesDataSource.savePreferences { prefs ->
            prefs.copy(
                propertyStaysSelectedPeriodStartMonth = selectedStartMonth,
                propertyStaysSelectedPeriodStartYear = selectedStartYear,
                propertyStaysSelectedPeriodEndMonth = selectedEndMonth,
                propertyStaysSelectedPeriodEndYear = selectedEndYear,
            )
        }
            .onError { return AppResult.Error(error = it) }

        return AppResult.Success(data = Unit)
    }
}
