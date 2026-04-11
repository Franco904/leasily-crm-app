package com.fstengineering.leasilycrm.core.infra.repositories

import com.fstengineering.leasilycrm.core.application.repositories.PropertySharedCommandRepository
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.entities.Property
import com.fstengineering.leasilycrm.core.domain.valueObjects.CalendarMonthYear
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.infra.adapters.interfaces.DatabaseAdapter
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.PropertyDatabaseDataSource
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

class PropertySharedCommandRepositoryImpl(
    private val databaseAdapter: DatabaseAdapter,
    private val propertyDatabaseDataSource: PropertyDatabaseDataSource,
) : PropertySharedCommandRepository {
    override suspend fun delete(id: Long): AppResult<Unit, ApplicationError> {
        return databaseAdapter.withTransaction {
            propertyDatabaseDataSource.delete(id)
        }
            .fold(
                onError = { AppResult.Error(error = it.toApplicationError()) },
                onSuccess = { AppResult.Success(data = Unit) }
            )
    }

    override suspend fun getAllMatching(
        matching: String?,
    ): AppResult<ImmutableList<Property>, ApplicationError> {
        val properties = propertyDatabaseDataSource.getAll(matching)
            .fold(
                onError = { return AppResult.Error(error = it.toApplicationError()) },
                onSuccess = { it },
            )
            .map { property -> property.toDomain() }.toPersistentList()

        return AppResult.Success(data = properties)
    }

    override fun watchAll(): Flow<AppResult<ImmutableList<Property>, ApplicationError>> =
        flow {
            val propertiesFlow = propertyDatabaseDataSource.watchAll()
                .fold(
                    onError = { return@flow emit(AppResult.Error(error = it.toApplicationError())) },
                    onSuccess = { it },
                )
                .map {
                    val properties = it.map { property -> property.toDomain() }.toPersistentList()
                    AppResult.Success<ImmutableList<Property>, ApplicationError>(data = properties)
                }
                .distinctUntilChanged()

            emitAll(propertiesFlow)
        }
}
