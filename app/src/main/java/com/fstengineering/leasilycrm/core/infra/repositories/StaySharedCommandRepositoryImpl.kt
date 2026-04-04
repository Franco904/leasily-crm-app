package com.fstengineering.leasilycrm.core.infra.repositories

import com.fstengineering.leasilycrm.core.application.repositories.StaySharedCommandRepository
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.entities.Stay
import com.fstengineering.leasilycrm.core.domain.helpers.StayCheckStatus
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.infra.adapters.interfaces.DatabaseAdapter
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.StayEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.StayFileEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.StayTransferEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.StayDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.StayFileDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.StayTransferDatabaseDataSource
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

    override suspend fun getById(stayId: Long): AppResult<Stay, ApplicationError> {
        val stay = stayDatabaseDataSource.getById(id = stayId)
            .fold(
                onError = { return AppResult.Error(error = it.toApplicationError()) },
                onSuccess = { it.toDomain() },
            )

        return AppResult.Success(data = stay)
    }

    override fun watchById(stayId: Long): Flow<AppResult<Stay?, ApplicationError>> =
        flow {
            val stayFlow = stayDatabaseDataSource.watchById(id = stayId)
                .fold(
                    onError = { return@flow emit(AppResult.Error(error = it.toApplicationError())) },
                    onSuccess = { it },
                )
                .map { stayEntity ->
                    val stay = stayEntity?.toDomain() ?: return@map AppResult.Success(data = null)

                    AppResult.Success<Stay, ApplicationError>(data = stay)
                }
                .distinctUntilChanged()

            emitAll(stayFlow)
        }

    override fun watchAll(): Flow<AppResult<ImmutableList<Stay>, ApplicationError>> =
        flow {
            val staysFlow = stayDatabaseDataSource.watchAll()
                .fold(
                    onError = { return@flow emit(AppResult.Error(error = it.toApplicationError())) },
                    onSuccess = { it },
                )
                .map {
                    val stays = it.map { stay -> stay.toDomain() }.toPersistentList()
                    AppResult.Success<ImmutableList<Stay>, ApplicationError>(data = stays)
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
}
