package com.fstengineering.leasilycrm.core.data.repositories

import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.CustomerSegmentDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.CustomerSegmentEntity
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.encrypted.EncryptedPreferences
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.interfaces.PreferencesStorage
import com.fstengineering.leasilycrm.core.domain.models.CustomerSegment
import com.fstengineering.leasilycrm.core.domain.repositories.CustomerSegmentRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError
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
    ): AppResult<Unit, DataSourceError> {
        val userRemoteId = encryptedPreferencesStorage.readPreferences()
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { prefsFlow ->
                    prefsFlow.first().remoteId ?: return AppResult.Error(
                        error = DataSourceError.PreferencesError.NotFound,
                    )
                }
            )

        val customerSegmentWithUser = customerSegment.copy(userId = userRemoteId)
        val entity = CustomerSegmentEntity.fromDomain(customerSegmentWithUser)

        dao.insertOrUpdate(entity)

        return AppResult.Success(data = Unit)
    }

    override suspend fun delete(id: Long): AppResult<Unit, DataSourceError> {
        dao.delete(id)

        return AppResult.Success(data = Unit)
    }

    override suspend fun getAllTenantType(): AppResult<ImmutableList<CustomerSegment>, DataSourceError> {
        val userRemoteId = encryptedPreferencesStorage.readPreferences()
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { prefsFlow ->
                    prefsFlow.first().remoteId ?: run {
                        return AppResult.Error(error = DataSourceError.PreferencesError.NotFound)
                    }
                }
            )

        val customerSegments = dao.getAllTenantType(userId = userRemoteId)
            .map { it.toDomain() }.toPersistentList()

        return AppResult.Success(data = customerSegments)
    }

    override suspend fun getAllPropertyOwnerType(): AppResult<ImmutableList<CustomerSegment>, DataSourceError> {
        val userRemoteId = encryptedPreferencesStorage.readPreferences()
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { prefsFlow ->
                    prefsFlow.first().remoteId ?: run {
                        return AppResult.Error(error = DataSourceError.PreferencesError.NotFound)
                    }
                }
            )

        val customerSegments = dao.getAllPropertyOwnerType(userId = userRemoteId)
            .map { it.toDomain() }.toPersistentList()

        return AppResult.Success(data = customerSegments)
    }

    override fun watchAll(): Flow<AppResult<ImmutableList<CustomerSegment>, DataSourceError>> =
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

            emitAll(
                dao.watchAll(userId = userRemoteId)
                    .map {
                        val customerSegments = it.map { it.toDomain() }.toPersistentList()
                        AppResult.Success<ImmutableList<CustomerSegment>, DataSourceError>(data = customerSegments)
                    }
                    .distinctUntilChanged()
            )
        }

    override suspend fun isThereCustomerSegmentWithName(
        customerSegmentName: String,
        customerSegmentTypeId: Long,
        currentStoredCustomerSegmentName: String?,
    ): AppResult<Boolean, DataSourceError> {
        val userRemoteId = encryptedPreferencesStorage.readPreferences()
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { prefsFlow ->
                    prefsFlow.first().remoteId ?: run {
                        return AppResult.Error(error = DataSourceError.PreferencesError.NotFound)
                    }
                }
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
