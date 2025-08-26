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
    override suspend fun insert(
        customerSegment: CustomerSegment,
    ): AppResult<Unit, DataSourceError> {
        val userRemoteId = encryptedPreferencesStorage.readPreferences()
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { prefsFlow ->
                    prefsFlow.first().remoteId ?: return AppResult.Error(
                        error = DataSourceError.RemoteBackendError.NotFound,
                    )
                }
            )

        val customerSegmentWithUser = customerSegment.copy(userId = userRemoteId)
        val entity = CustomerSegmentEntity.fromDomain(customerSegmentWithUser)

        dao.insert(entity)

        return AppResult.Success(data = Unit)
    }

    override suspend fun update(
        customerSegment: CustomerSegment,
    ): AppResult<Unit, DataSourceError> {
        if (customerSegment.id == null) {
            return AppResult.Error(error = DataSourceError.RemoteBackendError.NotFound)
        }

        val storedUpdated = dao.getById(id = customerSegment.id).copy(
            name = customerSegment.name!!,
            customerSegmentTypeId = customerSegment.type.id!!,
        )

        dao.update(storedUpdated)

        return AppResult.Success(data = Unit)
    }

    override suspend fun delete(id: Int): AppResult<Unit, DataSourceError> {
        dao.delete(id)

        return AppResult.Success(data = Unit)
    }

    override suspend fun watchAll(): Flow<AppResult<ImmutableList<CustomerSegment>, DataSourceError>> =
        flow {
            val userRemoteId = encryptedPreferencesStorage.readPreferences()
                .fold(
                    onError = {
                        emit(AppResult.Error(error = it))
                        return@flow
                    },
                    onSuccess = { prefsFlow ->
                        prefsFlow.first().remoteId ?: run {
                            emit(AppResult.Error(error = DataSourceError.RemoteBackendError.NotFound))
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
}
