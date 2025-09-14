package com.fstengineering.leasilycrm.core.data.repositories

import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.TenantDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.TenantEntity
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.encrypted.EncryptedPreferences
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.interfaces.PreferencesStorage
import com.fstengineering.leasilycrm.core.domain.models.Tenant
import com.fstengineering.leasilycrm.core.domain.repositories.TenantRepository
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
import java.time.LocalDate

class TenantRepositoryImpl(
    private val dao: TenantDao,
    private val encryptedPreferencesStorage: PreferencesStorage<EncryptedPreferences>,
) : TenantRepository {
    override suspend fun insertOrUpdate(
        tenant: Tenant,
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

        val tenantWithUser = tenant.copy(
            userId = userRemoteId,
            registerDate = LocalDate.now(),
        )
        val tenantEntity = TenantEntity.fromDomain(tenantWithUser)

        dao.insertOrUpdate(tenantEntity)

        return AppResult.Success(data = Unit)
    }

    override suspend fun delete(id: Long): AppResult<Unit, DataSourceError> {
        dao.delete(id)

        return AppResult.Success(data = Unit)
    }

    override suspend fun getById(
        tenantId: Long,
    ): AppResult<Tenant, DataSourceError> {
        val tenant = dao.getById(id = tenantId)
            .toDomain()

        return AppResult.Success(data = tenant)
    }

    override suspend fun getAllCityNames(): AppResult<ImmutableList<String>, DataSourceError> {
        val userRemoteId = encryptedPreferencesStorage.readPreferences()
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { prefsFlow ->
                    prefsFlow.first().remoteId ?: return AppResult.Error(
                        error = DataSourceError.PreferencesError.NotFound,
                    )
                }
            )

        val cityNames =
            dao.getAllCityNames(userId = userRemoteId).toPersistentList()

        return AppResult.Success(data = cityNames)
    }

    override fun watchById(
        tenantId: Long,
    ): Flow<AppResult<Tenant, DataSourceError>> = flow {
        val tenantFlow = dao.watchById(id = tenantId)
            .map { tenant ->
                val domain = tenant.toDomain()

                AppResult.Success<Tenant, DataSourceError>(domain)
            }
            .distinctUntilChanged()

        emitAll(tenantFlow)
    }

    override fun watchAll(): Flow<AppResult<ImmutableList<Tenant>, DataSourceError>> =
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
                        val tenants = it.map { tenant -> tenant.toDomain() }.toPersistentList()
                        AppResult.Success<ImmutableList<Tenant>, DataSourceError>(data = tenants)
                    }
                    .distinctUntilChanged()
            )
        }
}
