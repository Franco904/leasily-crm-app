package com.fstengineering.leasilycrm.core.data.repositories

import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.StayDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.StayEntity
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.encrypted.EncryptedPreferences
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.interfaces.PreferencesStorage
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.plain.PlainPreferences
import com.fstengineering.leasilycrm.core.domain.models.Stay
import com.fstengineering.leasilycrm.core.domain.repositories.StayRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class StayRepositoryImpl(
    private val dao: StayDao,
    private val encryptedPreferencesStorage: PreferencesStorage<EncryptedPreferences>,
    private val plainPreferencesStorage: PreferencesStorage<PlainPreferences>,
) : StayRepository {
    override suspend fun insertOrUpdate(stay: Stay): AppResult<Long, DataSourceError> {
        val userRemoteId = encryptedPreferencesStorage.readPreferences()
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { prefsFlow ->
                    prefsFlow.first().remoteId ?: return AppResult.Error(
                        error = DataSourceError.PreferencesError.NotFound,
                    )
                }
            )

        val stayWithUser = stay.copy(userId = userRemoteId)
        val stayEntity = StayEntity.fromDomain(stayWithUser)

        val id = dao.insertOrUpdate(stayEntity)
            .let { stay.id ?: it }

        return AppResult.Success(data = id)
    }

    override suspend fun delete(id: Long): AppResult<Unit, DataSourceError> {
        dao.delete(id)

        return AppResult.Success(data = Unit)
    }

    override suspend fun getById(stayId: Long): AppResult<Stay, DataSourceError> {
        val stay = dao.getById(id = stayId)
            .toDomain()

        return AppResult.Success(data = stay)
    }

    override fun watchById(stayId: Long): Flow<AppResult<Stay?, DataSourceError>> = flow {
        val stayFlow = dao.watchById(id = stayId)
            .map { stay ->
                AppResult.Success<Stay?, DataSourceError>(data = stay?.toDomain())
            }
            .distinctUntilChanged()

        emitAll(stayFlow)
    }

    override fun watchAll(): Flow<AppResult<ImmutableList<Stay>, DataSourceError>> =
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

            val staysFlow = dao.watchAll(userId = userRemoteId)
                .map {
                    val stays = it.map { stay -> stay.toDomain() }.toPersistentList()
                    AppResult.Success<ImmutableList<Stay>, DataSourceError>(data = stays)
                }
                .distinctUntilChanged()

            emitAll(staysFlow)
        }

    override fun watchAllByProperty(
        propertyId: Long,
    ): Flow<AppResult<ImmutableList<Stay>, DataSourceError>> =
        flow {
            val staysFlow = dao.watchAllByProperty(propertyId = propertyId)
                .map {
                    val stays = it.map { stay -> stay.toDomain() }.toPersistentList()
                    AppResult.Success<ImmutableList<Stay>, DataSourceError>(data = stays)
                }
                .distinctUntilChanged()

            emitAll(staysFlow)
        }

    override fun watchAllByTenant(
        tenantId: Long,
    ): Flow<AppResult<ImmutableList<Stay>, DataSourceError>> =
        flow {
            val staysFlow = dao.watchAllByTenant(tenantId = tenantId)
                .map {
                    val stays = it.map { stay -> stay.toDomain() }.toPersistentList()
                    AppResult.Success<ImmutableList<Stay>, DataSourceError>(data = stays)
                }
                .distinctUntilChanged()

            emitAll(staysFlow)
        }

    override fun watchMustNotShowCheckOutDialogAnymore(): Flow<AppResult<Boolean, DataSourceError>> =
        flow {
            plainPreferencesStorage.readPreferences()
                .fold(
                    onError = { error ->
                        emit(AppResult.Error(error = error))
                        return@flow
                    },
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
    ): AppResult<Unit, DataSourceError> {
        plainPreferencesStorage.savePreferences { prefs ->
            prefs.copy(mustNotShowCheckOutDialogAnymore = mustNotShowCheckOutDialogAnymore)
        }
            .onError { return AppResult.Error(error = it) }

        return AppResult.Success(data = Unit)
    }
}
