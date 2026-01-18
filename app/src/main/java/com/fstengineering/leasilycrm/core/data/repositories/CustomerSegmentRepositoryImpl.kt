package com.fstengineering.leasilycrm.core.data.repositories

import com.fstengineering.leasilycrm.core.data.adapters.interfaces.DatabaseAdapter
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.CustomerSegmentEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces.CustomerSegmentDatabaseDataSource
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.encrypted.EncryptedPreferences
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.interfaces.PreferencesDataSource
import com.fstengineering.leasilycrm.core.domain.models.CustomerSegment
import com.fstengineering.leasilycrm.core.domain.repositories.CustomerSegmentRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.presentation.utils.normalizeEncoding
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class CustomerSegmentRepositoryImpl(
    private val databaseAdapter: DatabaseAdapter,
    private val databaseDataSource: CustomerSegmentDatabaseDataSource,
    private val encryptedPreferencesDataSource: PreferencesDataSource<EncryptedPreferences>,
) : CustomerSegmentRepository {
    override suspend fun insertOrUpdate(
        customerSegment: CustomerSegment,
    ): AppResult<Unit, DataError> {
        val userRemoteId = encryptedPreferencesDataSource.readPreferences()
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { prefsFlow -> prefsFlow.firstOrNull()?.remoteId ?: -1 }
            )

        val customerSegmentWithUser = customerSegment.copy(userId = userRemoteId)
        val entity = CustomerSegmentEntity.fromDomain(customerSegmentWithUser)

        return databaseAdapter.withTransaction {
            databaseDataSource.insertOrUpdate(entity)
        }
    }

    override suspend fun delete(id: Long): AppResult<Unit, DataError> =
        databaseAdapter.withTransaction { databaseDataSource.delete(id) }

    override suspend fun getAll(): AppResult<ImmutableList<CustomerSegment>, DataError> {
        val userRemoteId = encryptedPreferencesDataSource.readPreferences()
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { prefsFlow -> prefsFlow.firstOrNull()?.remoteId ?: -1 }
            )

        val customerSegments = databaseDataSource.getAll(userId = userRemoteId)
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { customerSegments ->
                    customerSegments.map { it.toDomain() }.toPersistentList()
                }
            )

        return AppResult.Success(data = customerSegments)
    }
}
