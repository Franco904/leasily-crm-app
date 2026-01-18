package com.fstengineering.leasilycrm.core.data.repositories

import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.StayEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces.StayDatabaseDataSource
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.encrypted.EncryptedPreferences
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.interfaces.PreferencesDataSource
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.plain.PlainPreferences
import com.fstengineering.leasilycrm.core.data.dataSources.sharedStorage.interfaces.SharedStorageDataSource
import com.fstengineering.leasilycrm.core.domain.models.Stay
import com.fstengineering.leasilycrm.core.domain.models.StayCheckStatus
import com.fstengineering.leasilycrm.core.domain.repositories.StayRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class StayRepositoryImpl(
    private val databaseDataSource: StayDatabaseDataSource,
    private val encryptedPreferencesDataSource: PreferencesDataSource<EncryptedPreferences>,
    private val plainPreferencesDataSource: PreferencesDataSource<PlainPreferences>,
    private val sharedStorageDataSource: SharedStorageDataSource,
) : StayRepository {
    override suspend fun insertOrUpdate(stay: Stay): AppResult<Long, DataError> {
        val userRemoteId = encryptedPreferencesDataSource.readPreferences()
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { prefsFlow -> prefsFlow.firstOrNull()?.remoteId ?: -1 },
            )

        val stayWithUser = stay.copy(userId = userRemoteId)
        val stayEntity = StayEntity.fromDomain(stayWithUser)

        val id = databaseDataSource.insertOrUpdate(stayEntity)
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { it },
            )
            .let { stay.id ?: it }

        return AppResult.Success(data = id)
    }

    override suspend fun delete(id: Long): AppResult<Unit, DataError> {
        databaseDataSource.delete(id)
            .onError { return AppResult.Error(error = it) }

        return AppResult.Success(data = Unit)
    }

    override suspend fun getById(stayId: Long): AppResult<Stay, DataError> {
        val stay = databaseDataSource.getById(id = stayId)
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { it },
            )
            .toDomain()

        val filesWithNames = stay.files.map { file ->
            val fileName = sharedStorageDataSource
                .getFileName(contentUriString = file.uriString ?: "")

            file.copy(name = fileName)
        }.toImmutableList()

        return AppResult.Success(data = stay.copy(files = filesWithNames))
    }

    override fun watchById(stayId: Long): Flow<AppResult<Stay?, DataError>> = flow {
        val stayFlow = databaseDataSource.watchById(id = stayId)
            .fold(
                onError = { return@flow emit(AppResult.Error(error = it)) },
                onSuccess = { it },
            )
            .map { stay ->
                val domain = stay?.toDomain() ?: return@map AppResult.Success(data = null)

                val filesWithNames = domain.files.map { file ->
                    val fileName =
                        sharedStorageDataSource.getFileName(contentUriString = file.uriString ?: "")

                    file.copy(name = fileName)
                }.toImmutableList()

                AppResult.Success<Stay?, DataError>(data = domain.copy(files = filesWithNames))
            }
            .distinctUntilChanged()

        emitAll(stayFlow)
    }

    override fun watchAll(): Flow<AppResult<ImmutableList<Stay>, DataError>> =
        flow {
            val userRemoteId = encryptedPreferencesDataSource.readPreferences()
                .fold(
                    onError = {
                        emit(AppResult.Error(error = it))
                        return@flow
                    },
                    onSuccess = { prefsFlow -> prefsFlow.firstOrNull()?.remoteId ?: -1 },
                )

            val staysFlow = databaseDataSource.watchAll(userId = userRemoteId)
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
            val userRemoteId = encryptedPreferencesDataSource.readPreferences()
                .fold(
                    onError = {
                        emit(AppResult.Error(error = it))
                        return@flow
                    },
                    onSuccess = { prefsFlow -> prefsFlow.firstOrNull()?.remoteId ?: -1 },
                )

            val staysStatusFlow = databaseDataSource
                .watchAllUpcomingCheckInsAndCheckOuts(userId = userRemoteId)
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
