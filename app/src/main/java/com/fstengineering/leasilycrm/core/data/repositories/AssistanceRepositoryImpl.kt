package com.fstengineering.leasilycrm.core.data.repositories

import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.AssistanceEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces.AssistanceDatabaseDataSource
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.encrypted.EncryptedPreferences
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.interfaces.PreferencesDataSource
import com.fstengineering.leasilycrm.core.domain.models.Assistance
import com.fstengineering.leasilycrm.core.domain.repositories.AssistanceRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.firstOrNull

class AssistanceRepositoryImpl(
    private val databaseDataSource: AssistanceDatabaseDataSource,
    private val encryptedPreferencesDataSource: PreferencesDataSource<EncryptedPreferences>,
) : AssistanceRepository {
    override suspend fun insertOrUpdate(assistance: Assistance): AppResult<Long, DataError> {
        val assistanceEntity = AssistanceEntity.fromDomain(assistance)

        val id = databaseDataSource.insertOrUpdate(assistanceEntity)
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { id -> id.let { assistance.id ?: it } }
            )

        return AppResult.Success(data = id)
    }

    override suspend fun delete(id: Long): AppResult<Unit, DataError> {
        databaseDataSource.delete(id)
            .onError { return AppResult.Error(error = it) }

        return AppResult.Success(data = Unit)
    }

    override suspend fun getById(assistanceId: Long): AppResult<Assistance, DataError> {
        val property = databaseDataSource.getById(id = assistanceId)
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { property -> property.toDomain() }
            )

        return AppResult.Success(data = property)
    }

    override suspend fun getAllNeighborhoodNames(): AppResult<ImmutableList<String>, DataError> {
        val userRemoteId = encryptedPreferencesDataSource.readPreferences()
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { prefsFlow -> prefsFlow.firstOrNull()?.remoteId ?: -1 }
            )

        val neighborhoodNames = databaseDataSource.getAllNeighborhoodNames(userId = userRemoteId)
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { neighborhoodNames -> neighborhoodNames.toPersistentList() }
            )

        return AppResult.Success(data = neighborhoodNames)
    }
}
