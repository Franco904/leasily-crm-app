package com.fstengineering.leasilycrm.core.data.repositories

import com.fstengineering.leasilycrm.core.domain.entities.Stay
import com.fstengineering.leasilycrm.core.domain.helpers.StayCheckStatus
import com.fstengineering.leasilycrm.core.domain.repositories.StayRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.infra.adapters.interfaces.DatabaseAdapter
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.StayEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.StayFileEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.StayTransferEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.StayDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.StayFileDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.StayTransferDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.encrypted.EncryptedPreferences
import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.interfaces.PreferencesDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.plain.PlainPreferences
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class StayRepositoryImpl(
    private val databaseAdapter: DatabaseAdapter,
    private val stayDatabaseDataSource: StayDatabaseDataSource,
    private val stayFileDatabaseDataSource: StayFileDatabaseDataSource,
    private val stayTransferDatabaseDataSource: StayTransferDatabaseDataSource,
    private val plainPreferencesDataSource: PreferencesDataSource<PlainPreferences>,
) : StayRepository {
    override suspend fun insertOrUpdate(stay: Stay): AppResult<Unit, DataError> {
        return databaseAdapter.withTransaction {
            saveDataInDatabase(stay)
        }
    }

    private suspend fun saveDataInDatabase(stay: Stay) {
        val stayEntity = StayEntity.fromDomain(stay)

        val stayId = stayDatabaseDataSource.insertOrUpdate(stayEntity)
            .let { stay.id ?: it }

        if (stay is Stay.Owned) {
            stay.files.forEach {
                val file = StayFileEntity.fromDomain(domain = it.copy(stayId = stayId))

                stayFileDatabaseDataSource.insertOrUpdate(file)
            }

            stay.transfers.forEach {
                val transfer = StayTransferEntity.fromDomain(it, stayId)

                stayTransferDatabaseDataSource.insertOrUpdate(transfer)
            }
        }
    }

    override suspend fun delete(id: Long): AppResult<Unit, DataError> =
        databaseAdapter.withTransaction {
            stayDatabaseDataSource.delete(id)
        }

    override suspend fun getById(stayId: Long): AppResult<Stay, DataError> {
        val stay = stayDatabaseDataSource.getById(id = stayId)
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { it.toDomain() },
            )

        return AppResult.Success(data = stay)
    }

    override fun watchById(stayId: Long): Flow<AppResult<Stay?, DataError>> =
        flow {
            val stayFlow = stayDatabaseDataSource.watchById(id = stayId)
                .fold(
                    onError = { return@flow emit(AppResult.Error(error = it)) },
                    onSuccess = { it },
                )
                .map { stayEntity ->
                    val stay = stayEntity?.toDomain() ?: return@map AppResult.Success(data = null)

                    AppResult.Success<Stay, DataError>(data = stay)
                }
                .distinctUntilChanged()

            emitAll(stayFlow)
        }

    override fun watchAll(): Flow<AppResult<ImmutableList<Stay>, DataError>> =
        flow {
            val staysFlow = stayDatabaseDataSource.watchAll()
                .fold(
                    onError = { return@flow emit(AppResult.Error(error = it)) },
                    onSuccess = { it },
                )
                .map {
                    val stays = it.map { stay -> stay.toDomain() }.toPersistentList()
                    AppResult.Success<ImmutableList<Stay>, DataError>(data = stays)
                }
                .distinctUntilChanged()

            emitAll(staysFlow)
        }

    override fun watchAllUpcomingCheckInsAndCheckOuts(): Flow<AppResult<ImmutableList<StayCheckStatus>, DataError>> =
        flow {
            val staysStatusFlow = stayDatabaseDataSource
                .watchAllUpcomingCheckInsAndCheckOuts()
                .fold(
                    onError = { return@flow emit(AppResult.Error(error = it)) },
                    onSuccess = { it },
                )
                .map {
                    val staysStatus =
                        it.map { stayStatus -> stayStatus.toDomain() }.toPersistentList()
                    AppResult.Success<ImmutableList<StayCheckStatus>, DataError>(data = staysStatus)
                }
                .distinctUntilChanged()

            emitAll(staysStatusFlow)
        }

    override fun watchMustNotShowCheckOutDialogAnymore(): Flow<AppResult<Boolean, DataError>> =
        flow {
            plainPreferencesDataSource.readPreferences()
                .fold(
                    onError = { error -> return@flow emit(AppResult.Error(error = error)) },
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
    ): AppResult<Unit, DataError> {
        plainPreferencesDataSource.savePreferences { prefs ->
            prefs.copy(mustNotShowCheckOutDialogAnymore = mustNotShowCheckOutDialogAnymore)
        }
            .onError { return AppResult.Error(error = it) }

        return AppResult.Success(data = Unit)
    }
}
