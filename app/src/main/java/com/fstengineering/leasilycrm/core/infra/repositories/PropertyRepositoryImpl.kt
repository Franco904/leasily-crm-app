package com.fstengineering.leasilycrm.core.infra.repositories

import com.fstengineering.leasilycrm.core.application.repositories.PropertyRepository
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.entities.Property
import com.fstengineering.leasilycrm.core.domain.helpers.CalendarMonthYear
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.infra.adapters.interfaces.DatabaseAdapter
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyCharacteristicCrossRefEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyFileEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyPriceEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.PropertyCharacteristicCrossRefDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.PropertyDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.PropertyFileDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.PropertyPriceDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.interfaces.PreferencesDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.plain.PlainPreferences
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest

class PropertyRepositoryImpl(
    private val databaseAdapter: DatabaseAdapter,
    private val propertyDatabaseDataSource: PropertyDatabaseDataSource,
    private val propertyCharacteristicCrossRefDatabaseDataSource: PropertyCharacteristicCrossRefDatabaseDataSource,
    private val propertyFileDatabaseDataSource: PropertyFileDatabaseDataSource,
    private val propertyPriceDatabaseDataSource: PropertyPriceDatabaseDataSource,
    private val plainPreferencesDataSource: PreferencesDataSource<PlainPreferences>,
) : PropertyRepository {
    override suspend fun insertOrUpdate(property: Property): AppResult<Unit, ApplicationError> {
        return databaseAdapter.withTransaction {
            saveDataInDatabase(property)
        }
            .fold(
                onError = { AppResult.Error(error = it.toApplication()) },
                onSuccess = { AppResult.Success(data = Unit) }
            )
    }

    private suspend fun saveDataInDatabase(property: Property) {
        val propertyEntity = PropertyEntity.fromDomain(property)

        val propertyId = propertyDatabaseDataSource.insertOrUpdate(propertyEntity)
            .let { property.id ?: it }

        property.characteristics.forEach {
            val characteristic = PropertyCharacteristicCrossRefEntity.fromDomain(it, propertyId)
            propertyCharacteristicCrossRefDatabaseDataSource.insertOrUpdate(characteristic)
        }

        property.files.forEach {
            val file = PropertyFileEntity.fromDomain(it, propertyId)
            propertyFileDatabaseDataSource.insertOrUpdate(file)
        }

        property.prices.forEach {
            val price = PropertyPriceEntity.fromDomain(it, propertyId)
            propertyPriceDatabaseDataSource.insertOrUpdate(price)
        }
    }

    override suspend fun delete(id: Long): AppResult<Unit, ApplicationError> {
        return databaseAdapter.withTransaction {
            propertyDatabaseDataSource.delete(id)
        }
            .fold(
                onError = { AppResult.Error(error = it.toApplication()) },
                onSuccess = { AppResult.Success(data = Unit) }
            )
    }

    override suspend fun getById(propertyId: Long): AppResult<Property, ApplicationError> {
        val property = propertyDatabaseDataSource.getById(id = propertyId)
            .fold(
                onError = { return AppResult.Error(error = it.toApplication()) },
                onSuccess = { it },
            )
            .toDomain()

        return AppResult.Success(data = property)
    }

    override suspend fun getAll(): AppResult<ImmutableList<Property>, ApplicationError> {
        val properties = propertyDatabaseDataSource.getAll()
            .fold(
                onError = { return AppResult.Error(error = it.toApplication()) },
                onSuccess = { it },
            )
            .map { property -> property.toDomain() }.toPersistentList()

        return AppResult.Success(data = properties)
    }

    override suspend fun getAllMatching(
        matching: String?,
    ): AppResult<ImmutableList<Property>, ApplicationError> {
        val properties = propertyDatabaseDataSource.getAllMatching(matching)
            .fold(
                onError = { return AppResult.Error(error = it.toApplication()) },
                onSuccess = { it },
            )
            .map { property -> property.toDomain() }.toPersistentList()

        return AppResult.Success(data = properties)
    }

    override suspend fun getAllNeighborHoodNames(): AppResult<ImmutableList<String>, ApplicationError> {
        val neighborHoodNames =
            propertyDatabaseDataSource.getAllNeighborHoodNames()
                .fold(
                    onError = { return AppResult.Error(error = it.toApplication()) },
                    onSuccess = { it },
                )
                .toPersistentList()

        return AppResult.Success(data = neighborHoodNames)
    }

    override fun watchById(propertyId: Long): Flow<AppResult<Property?, ApplicationError>> = flow {
        val propertyFlow = propertyDatabaseDataSource.watchById(id = propertyId)
            .fold(
                onError = { return@flow emit(AppResult.Error(error = it.toApplication())) },
                onSuccess = { it },
            )
            .map { property ->
                AppResult.Success<Property?, ApplicationError>(data = property?.toDomain())
            }
            .distinctUntilChanged()

        emitAll(propertyFlow)
    }

    override fun watchAll(): Flow<AppResult<ImmutableList<Property>, ApplicationError>> =
        flow {
            val propertiesFlow = propertyDatabaseDataSource.watchAll()
                .fold(
                    onError = { return@flow emit(AppResult.Error(error = it.toApplication())) },
                    onSuccess = { it },
                )
                .map {
                    val properties = it.map { property -> property.toDomain() }.toPersistentList()
                    AppResult.Success<ImmutableList<Property>, ApplicationError>(data = properties)
                }
                .distinctUntilChanged()

            emitAll(propertiesFlow)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun watchPropertyStaysSelectedPeriod(): Flow<AppResult<Pair<CalendarMonthYear?, CalendarMonthYear?>, ApplicationError>> {
        return plainPreferencesDataSource.readPreferences()
            .fold(
                onError = { error ->
                    flow { emit(AppResult.Error(error = error.toApplication())) }
                },
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
    ): AppResult<Unit, ApplicationError> {
        plainPreferencesDataSource.savePreferences { prefs ->
            prefs.copy(
                propertyStaysSelectedPeriodStartMonth = selectedStartMonth,
                propertyStaysSelectedPeriodStartYear = selectedStartYear,
                propertyStaysSelectedPeriodEndMonth = selectedEndMonth,
                propertyStaysSelectedPeriodEndYear = selectedEndYear,
            )
        }
            .onError { return AppResult.Error(error = it.toApplication()) }

        return AppResult.Success(data = Unit)
    }
}
