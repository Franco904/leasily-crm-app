package com.fstengineering.leasilycrm.core.data.repositories

import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.PropertyDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyEntity
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.encrypted.EncryptedPreferences
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.interfaces.PreferencesStorage
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest

class PropertyRepositoryImpl(
    private val dao: PropertyDao,
    private val encryptedPreferencesStorage: PreferencesStorage<EncryptedPreferences>,
    private val plainPreferencesStorage: PreferencesStorage<PlainPreferences>,
) : PropertyRepository {
    override suspend fun insertOrUpdate(property: Property): AppResult<Long, DataError> {
        val userRemoteId = encryptedPreferencesStorage.readPreferences()
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { prefsFlow -> prefsFlow.first().remoteId ?: -1 },
            )

        val propertyWithUser = property.copy(userId = userRemoteId)
        val propertyEntity = PropertyEntity.fromDomain(propertyWithUser)

        val id = dao.insertOrUpdate(propertyEntity)
            .let { property.id ?: it }

        return AppResult.Success(data = id)
    }

    override suspend fun delete(id: Long): AppResult<Unit, DataError> {
        dao.delete(id)

        return AppResult.Success(data = Unit)
    }

    override suspend fun getById(propertyId: Long): AppResult<Property, DataError> {
        val property = dao.getById(id = propertyId)
            .toDomain()

        return AppResult.Success(data = property)
    }

    override suspend fun getAll(): AppResult<ImmutableList<Property>, DataError> {
        val userRemoteId = encryptedPreferencesStorage.readPreferences()
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { prefsFlow -> prefsFlow.first().remoteId ?: -1 },
            )

        val properties = dao.getAll(userId = userRemoteId)
            .map { property -> property.toDomain() }.toPersistentList()

        return AppResult.Success(data = properties)
    }

    override suspend fun getAllMatching(
        matching: String?,
    ): AppResult<ImmutableList<Property>, DataError> {
        val userRemoteId = encryptedPreferencesStorage.readPreferences()
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { prefsFlow -> prefsFlow.first().remoteId ?: -1 },
            )

        val properties = dao.getAllMatching(
            userId = userRemoteId,
            matching = matching,
        )
            .map { property -> property.toDomain() }.toPersistentList()

        return AppResult.Success(data = properties)
    }

    override suspend fun getAllNeighborHoodNames(): AppResult<ImmutableList<String>, DataError> {
        val userRemoteId = encryptedPreferencesStorage.readPreferences()
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { prefsFlow -> prefsFlow.first().remoteId ?: -1 },
            )

        val neighborHoodNames =
            dao.getAllNeighborHoodNames(userId = userRemoteId).toPersistentList()

        return AppResult.Success(data = neighborHoodNames)
    }

    override fun watchById(propertyId: Long): Flow<AppResult<Property?, DataError>> = flow {
        val propertyFlow = dao.watchById(id = propertyId)
            .map { property ->
                AppResult.Success<Property?, DataError>(data = property?.toDomain())
            }
            .distinctUntilChanged()

        emitAll(propertyFlow)
    }

    override fun watchAll(): Flow<AppResult<ImmutableList<Property>, DataError>> =
        flow {
            val userRemoteId = encryptedPreferencesStorage.readPreferences()
                .fold(
                    onError = {
                        emit(AppResult.Error(error = it))
                        return@flow
                    },
                    onSuccess = { prefsFlow -> prefsFlow.first().remoteId ?: -1 },
                )

            val propertiesFlow = dao.watchAll(userId = userRemoteId)
                .map {
                    val properties = it.map { property -> property.toDomain() }.toPersistentList()
                    AppResult.Success<ImmutableList<Property>, DataError>(data = properties)
                }
                .distinctUntilChanged()

            emitAll(propertiesFlow)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun watchPropertyStaysSelectedPeriod(): Flow<AppResult<Pair<CalendarMonthYear?, CalendarMonthYear?>, DataError>> {
        return plainPreferencesStorage.readPreferences()
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
        plainPreferencesStorage.savePreferences { prefs ->
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
