package com.fstengineering.leasilycrm.core.data.repositories

import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.AssistanceDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.AssistanceEntity
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.encrypted.EncryptedPreferences
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.interfaces.PreferencesStorage
import com.fstengineering.leasilycrm.core.domain.models.Assistance
import com.fstengineering.leasilycrm.core.domain.repositories.AssistanceRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class AssistanceRepositoryImpl(
    private val dao: AssistanceDao,
    private val encryptedPreferencesStorage: PreferencesStorage<EncryptedPreferences>,
) : AssistanceRepository {
    override suspend fun insertOrUpdate(assistance: Assistance): AppResult<Long, DataError> {
        val assistanceEntity = AssistanceEntity.fromDomain(assistance)

        val id = dao.insertOrUpdate(assistanceEntity)
            .let { assistance.id ?: it }

        return AppResult.Success(data = id)
    }

    override suspend fun delete(id: Long): AppResult<Unit, DataError> {
        dao.delete(id)

        return AppResult.Success(data = Unit)
    }

    override suspend fun getById(assistanceId: Long): AppResult<Assistance, DataError> {
        val property = dao.getById(id = assistanceId)
            .toDomain()

        return AppResult.Success(data = property)
    }

    override suspend fun getAllNeighborhoodNames(): AppResult<ImmutableList<String>, DataError> {
        val userRemoteId = encryptedPreferencesStorage.readPreferences()
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { prefsFlow -> prefsFlow.first().remoteId ?: -1 }
            )

        val neighborhoodNames =
            dao.getAllNeighborhoodNames(userId = userRemoteId).toPersistentList()

        return AppResult.Success(data = neighborhoodNames)
    }

    override fun watchById(assistanceId: Long): Flow<AppResult<Assistance?, DataError>> =
        flow {
            val assistanceFlow = dao.watchById(id = assistanceId)
                .map { assistance ->
                    AppResult.Success<Assistance?, DataError>(data = assistance?.toDomain())
                }
                .distinctUntilChanged()

            emitAll(assistanceFlow)
        }

    override fun watchAllByTenant(
        tenantId: Long,
    ): Flow<AppResult<ImmutableList<Assistance>, DataError>> =
        flow {
            val assistancesFlow = dao.watchAllByTenant(tenantId = tenantId)
                .map {
                    val assistances =
                        it.map { assistance -> assistance.toDomain() }.toPersistentList()
                    AppResult.Success<ImmutableList<Assistance>, DataError>(data = assistances)
                }
                .distinctUntilChanged()

            emitAll(assistancesFlow)
        }
}
