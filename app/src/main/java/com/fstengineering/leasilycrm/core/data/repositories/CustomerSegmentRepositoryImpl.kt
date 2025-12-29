package com.fstengineering.leasilycrm.core.data.repositories

import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.CustomerSegmentDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.CustomerSegmentEntity
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.encrypted.EncryptedPreferences
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.interfaces.PreferencesStorage
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class CustomerSegmentRepositoryImpl(
    private val dao: CustomerSegmentDao,
    private val encryptedPreferencesStorage: PreferencesStorage<EncryptedPreferences>,
) : CustomerSegmentRepository {
    override suspend fun insertOrUpdate(
        customerSegment: CustomerSegment,
    ): AppResult<Unit, DataError> {
        val userRemoteId = encryptedPreferencesStorage.readPreferences()
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { prefsFlow -> prefsFlow.first().remoteId ?: -1 }
            )

        val customerSegmentWithUser = customerSegment.copy(userId = userRemoteId)
        val entity = CustomerSegmentEntity.fromDomain(customerSegmentWithUser)

        dao.insertOrUpdate(entity)

        return AppResult.Success(data = Unit)
    }

    override suspend fun delete(id: Long): AppResult<Unit, DataError> {
        dao.delete(id)

        return AppResult.Success(data = Unit)
    }

    override suspend fun getAllTenantType(): AppResult<ImmutableList<CustomerSegment>, DataError> {
        val userRemoteId = encryptedPreferencesStorage.readPreferences()
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { prefsFlow -> prefsFlow.first().remoteId ?: -1 }
            )

        val customerSegments = dao.getAllTenantType(userId = userRemoteId)
            .map { it.toDomain() }.toPersistentList()

        return AppResult.Success(data = customerSegments)
    }

    override suspend fun getAllPropertyOwnerType(): AppResult<ImmutableList<CustomerSegment>, DataError> {
        val userRemoteId = encryptedPreferencesStorage.readPreferences()
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { prefsFlow -> prefsFlow.first().remoteId ?: -1 }
            )

        val customerSegments = dao.getAllPropertyOwnerType(userId = userRemoteId)
            .map { it.toDomain() }.toPersistentList()

        return AppResult.Success(data = customerSegments)
    }

    override fun watchAll(): Flow<AppResult<ImmutableList<CustomerSegment>, DataError>> =
        flow {
            val userRemoteId = encryptedPreferencesStorage.readPreferences()
                .fold(
                    onError = {
                        emit(AppResult.Error(error = it))
                        return@flow
                    },
                    onSuccess = { prefsFlow -> prefsFlow.first().remoteId ?: -1 }
                )

            emitAll(
                dao.watchAll(userId = userRemoteId)
                    .map {
                        val customerSegments = it.map { it.toDomain() }.toPersistentList()
                        AppResult.Success<ImmutableList<CustomerSegment>, DataError>(data = customerSegments)
                    }
                    .distinctUntilChanged()
            )
        }

    override suspend fun isThereCustomerSegmentWithName(
        customerSegmentName: String,
        customerSegmentTypeId: Long,
        currentStoredCustomerSegmentName: String?,
    ): AppResult<Boolean, DataError> {
        val userRemoteId = encryptedPreferencesStorage.readPreferences()
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { prefsFlow -> prefsFlow.first().remoteId ?: -1 }
            )

        val count = dao.countCustomerSegmentsWithName(
            userId = userRemoteId,
            customerSegmentName = customerSegmentName.normalizeEncoding(),
            customerSegmentTypeId = customerSegmentTypeId,
            currentStoredCustomerSegmentName = currentStoredCustomerSegmentName,
        )

        return AppResult.Success(data = count > 0)
    }
}
