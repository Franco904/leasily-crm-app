package com.fstengineering.leasilycrm.core.infra.repositories

import com.fstengineering.leasilycrm.core.application.models.StayListReadModel
import com.fstengineering.leasilycrm.core.application.repositories.StaySharedCommandRepository
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyType
import com.fstengineering.leasilycrm.core.domain.valueObjects.StayCheckStatus
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.infra.adapters.interfaces.DatabaseAdapter
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.StayListCompound
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.StayDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.interfaces.PreferencesDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.plain.PlainPreferences
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class StaySharedCommandRepositoryImpl(
    private val databaseAdapter: DatabaseAdapter,
    private val stayDatabaseDataSource: StayDatabaseDataSource,
    private val plainPreferencesDataSource: PreferencesDataSource<PlainPreferences>,
) : StaySharedCommandRepository {
    override suspend fun delete(id: Long): AppResult<Unit, ApplicationError> {
        return databaseAdapter.withTransaction {
            stayDatabaseDataSource.delete(id)
        }
            .fold(
                onError = { AppResult.Error(error = it.toApplicationError()) },
                onSuccess = { AppResult.Success(data = Unit) }
            )
    }

    override fun watchAll(): Flow<AppResult<ImmutableList<StayListReadModel>, ApplicationError>> =
        flow {
            val staysFlow = stayDatabaseDataSource.watchAllWithDetails()
                .fold(
                    onError = { return@flow emit(AppResult.Error(error = it.toApplicationError())) },
                    onSuccess = { it },
                )
                .map {
                    val stays = it.map { compound -> compound.toListReadModel() }.toPersistentList()
                    AppResult.Success<ImmutableList<StayListReadModel>, ApplicationError>(data = stays)
                }
                .distinctUntilChanged()

            emitAll(staysFlow)
        }

    override fun watchAllUpcomingCheckInsAndCheckOuts(): Flow<AppResult<ImmutableList<StayCheckStatus>, ApplicationError>> =
        flow {
            val staysStatusFlow = stayDatabaseDataSource
                .watchAllUpcomingCheckInsAndCheckOuts()
                .fold(
                    onError = { return@flow emit(AppResult.Error(error = it.toApplicationError())) },
                    onSuccess = { it },
                )
                .map {
                    val staysStatus =
                        it.map { stayStatus -> stayStatus.toDomain() }.toPersistentList()
                    AppResult.Success<ImmutableList<StayCheckStatus>, ApplicationError>(data = staysStatus)
                }
                .distinctUntilChanged()

            emitAll(staysStatusFlow)
        }

    override fun watchMustNotShowCheckOutDialogAnymore(): Flow<AppResult<Boolean, ApplicationError>> =
        flow {
            plainPreferencesDataSource.readPreferences()
                .fold(
                    onError = { return@flow emit(AppResult.Error(error = it.toApplicationError())) },
                    onSuccess = { prefsFlow ->
                        emitAll(
                            prefsFlow.map {
                                AppResult.Success(
                                    data = it.mustNotShowCheckOutDialogAnymore ?: false
                                )
                            }
                        )
                    }
                )
        }

    override suspend fun setMustNotShowCheckOutDialogAnymore(
        mustNotShowCheckOutDialogAnymore: Boolean,
    ): AppResult<Unit, ApplicationError> {
        plainPreferencesDataSource.savePreferences { prefs ->
            prefs.copy(mustNotShowCheckOutDialogAnymore = mustNotShowCheckOutDialogAnymore)
        }
            .onError { return AppResult.Error(error = it.toApplicationError()) }

        return AppResult.Success(data = Unit)
    }

    override suspend fun registerCheckIn(id: Long): AppResult<Unit, ApplicationError> {
        return databaseAdapter.withTransaction {
            stayDatabaseDataSource.updateStatus(id = id, status = 1)
        }
            .fold(
                onError = { AppResult.Error(error = it.toApplicationError()) },
                onSuccess = { AppResult.Success(data = Unit) }
            )
    }

    override suspend fun undoRegisterCheckIn(id: Long): AppResult<Unit, ApplicationError> {
        return databaseAdapter.withTransaction {
            stayDatabaseDataSource.updateStatus(id = id, status = 0)
        }
            .fold(
                onError = { AppResult.Error(error = it.toApplicationError()) },
                onSuccess = { AppResult.Success(data = Unit) }
            )
    }

    private fun StayListCompound.toListReadModel() = StayListReadModel(
        id = id,
        tenantId = tenantId,
        tenantName = tenantName,
        propertyType = propertyTypeId?.let { PropertyType.fromId(it) },
        propertyNeighborhood = neighborhood,
        checkInDateTime = checkInDateTime,
        checkInHourMinute = checkInDateTime.hour to checkInDateTime.minute,
        checkOutDateTime = checkOutDateTime,
        checkOutHourMinute = checkOutDateTime.hour to checkOutDateTime.minute,
        isAutoCheckIn = isAutoCheckIn ?: false,
        isAutoCheckOut = isAutoCheckOut ?: false,
        isCheckInDone = (status ?: 0) >= 1,
        isCheckOutDone = (status ?: 0) >= 2,
    )
}
