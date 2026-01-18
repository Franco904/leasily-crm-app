package com.fstengineering.leasilycrm.core.data.repositories

import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.CustomerSegmentEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces.CustomerSegmentDatabaseDataSource
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.encrypted.EncryptedPreferences
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.interfaces.PreferencesDataSource
import com.fstengineering.leasilycrm.core.domain.models.CustomerSegment
import com.fstengineering.leasilycrm.core.domain.repositories.CustomerSegmentRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
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

        databaseDataSource.insertOrUpdate(entity)
            .onError { return AppResult.Error(error = it) }

        return AppResult.Success(data = Unit)
    }

    override suspend fun delete(id: Long): AppResult<Unit, DataError> {
        databaseDataSource.delete(id)
            .onError { return AppResult.Error(error = it) }

        return AppResult.Success(data = Unit)
    }

    override suspend fun getAllTenantType(): AppResult<ImmutableList<CustomerSegment>, DataError> {
        val userRemoteId = encryptedPreferencesDataSource.readPreferences()
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { prefsFlow -> prefsFlow.firstOrNull()?.remoteId ?: -1 }
            )

        val customerSegments = databaseDataSource.getAllTenantType(userId = userRemoteId)
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { customerSegments ->
                    customerSegments.map { it.toDomain() }.toPersistentList()
                }
            )

        return AppResult.Success(data = customerSegments)
    }

    override suspend fun getAllPropertyOwnerType(): AppResult<ImmutableList<CustomerSegment>, DataError> {
        val userRemoteId = encryptedPreferencesDataSource.readPreferences()
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { prefsFlow -> prefsFlow.firstOrNull()?.remoteId ?: -1 }
            )

        val customerSegments = databaseDataSource.getAllPropertyOwnerType(userId = userRemoteId)
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { customerSegments ->
                    customerSegments.map { it.toDomain() }.toPersistentList()
                }
            )

        return AppResult.Success(data = customerSegments)
    }

    override fun watchAll(): Flow<AppResult<ImmutableList<CustomerSegment>, DataError>> =
        flow {
            val userRemoteId = encryptedPreferencesDataSource.readPreferences()
                .fold(
                    onError = {
                        emit(AppResult.Error(error = it))
                        return@flow
                    },
                    onSuccess = { prefsFlow -> prefsFlow.firstOrNull()?.remoteId ?: -1 }
                )

            val customerSegmentsFlow = databaseDataSource.watchAll(userId = userRemoteId)
                .fold(
                    onError = { return@flow emit(AppResult.Error(error = it)) },
                    onSuccess = { it }
                )

            val customerSegmentsFlowMapped = customerSegmentsFlow.map {
                val customerSegments = it.map { segment -> segment.toDomain() }.toPersistentList()
                AppResult.Success<ImmutableList<CustomerSegment>, DataError>(data = customerSegments)
            }
            .distinctUntilChanged()

            emitAll(customerSegmentsFlowMapped)
        }

    override suspend fun isThereCustomerSegmentWithName(
        customerSegmentName: String,
        customerSegmentTypeId: Long,
        currentStoredCustomerSegmentName: String?,
    ): AppResult<Boolean, DataError> {
        val userRemoteId = encryptedPreferencesDataSource.readPreferences()
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { prefsFlow -> prefsFlow.firstOrNull()?.remoteId ?: -1 }
            )

        val count = databaseDataSource.countCustomerSegmentsWithName(
            userId = userRemoteId,
            customerSegmentName = customerSegmentName.normalizeEncoding(),
            customerSegmentTypeId = customerSegmentTypeId,
            currentStoredCustomerSegmentName = currentStoredCustomerSegmentName,
        )
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { count -> count }
            )

        return AppResult.Success(data = count > 0)
    }
}
