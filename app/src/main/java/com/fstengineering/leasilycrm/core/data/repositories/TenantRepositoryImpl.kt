package com.fstengineering.leasilycrm.core.data.repositories

import com.fstengineering.leasilycrm.core.data.adapters.interfaces.DatabaseAdapter
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.TenantEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces.TenantDatabaseDataSource
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.encrypted.EncryptedPreferences
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.interfaces.PreferencesDataSource
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.plain.PlainPreferences
import com.fstengineering.leasilycrm.core.domain.models.Tenant
import com.fstengineering.leasilycrm.core.domain.models.enums.TenantStatus
import com.fstengineering.leasilycrm.core.domain.repositories.TenantRepository
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

class TenantRepositoryImpl(
    private val databaseAdapter: DatabaseAdapter,
    private val databaseDataSource: TenantDatabaseDataSource,
    private val encryptedPreferencesDataSource: PreferencesDataSource<EncryptedPreferences>,
    private val plainPreferencesDataSource: PreferencesDataSource<PlainPreferences>,
) : TenantRepository {
    override suspend fun insertOrUpdate(
        tenant: Tenant,
    ): AppResult<Unit, DataError> {
        val userRemoteId = encryptedPreferencesDataSource.readPreferences()
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { prefsFlow -> prefsFlow.firstOrNull()?.remoteId ?: -1 },
            )

        val tenantWithUser = tenant.copy(userId = userRemoteId)
        val tenantEntity = TenantEntity.fromDomain(tenantWithUser)

        return databaseAdapter.withTransaction {
            databaseDataSource.insertOrUpdate(tenantEntity)
        }
    }

    override suspend fun delete(id: Long): AppResult<Unit, DataError> =
        databaseAdapter.withTransaction {
            databaseDataSource.delete(id)
        }

    override suspend fun getById(
        tenantId: Long,
    ): AppResult<Tenant, DataError> {
        val tenant = databaseDataSource.getById(id = tenantId)
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { it?.toDomain() }
            )

        return tenant?.let { AppResult.Success(data = tenant) }
            ?: AppResult.Error(DataError.UnknownError)
    }

    override suspend fun getAllLeadsMatching(
        matching: String?,
        orderTypeId: Int,
    ): AppResult<ImmutableList<Tenant>, DataError> {
        val userRemoteId = encryptedPreferencesDataSource.readPreferences()
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { prefsFlow -> prefsFlow.firstOrNull()?.remoteId ?: -1 },
            )

        val leads = databaseDataSource.getAllLeadsMatching(
            userId = userRemoteId,
            matching = matching,
            order = orderTypeId,
        )
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { it },
            )
            .map { lead -> lead.toDomain() }
            .toPersistentList()

        return AppResult.Success(data = leads)
    }

    override suspend fun getAllTenantsMatching(
        matching: String?,
        orderTypeId: Int,
    ): AppResult<ImmutableList<Tenant>, DataError> {
        val userRemoteId = encryptedPreferencesDataSource.readPreferences()
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { prefsFlow -> prefsFlow.firstOrNull()?.remoteId ?: -1 },
            )

        val tenants = databaseDataSource.getAllTenantsMatching(
            userId = userRemoteId,
            matching = matching,
            order = orderTypeId,
        )
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { it },
            )
            .map { tenant -> tenant.toDomain() }
            .toPersistentList()

        return AppResult.Success(data = tenants)
    }

    override suspend fun getAllWithoutUpcomingStaysMatching(
        matching: String?,
        currentTenantId: Long?,
    ): AppResult<ImmutableList<Tenant>, DataError> {
        val userRemoteId = encryptedPreferencesDataSource.readPreferences()
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { prefsFlow -> prefsFlow.firstOrNull()?.remoteId ?: -1 },
            )

        val tenants = databaseDataSource.getAllWithoutUpcomingStaysMatching(
            userId = userRemoteId,
            matching = matching,
            currentTenantId = currentTenantId,
        )
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { it },
            )
            .map { tenant -> tenant.toDomain() }
            .toPersistentList()

        return AppResult.Success(data = tenants)
    }

    override suspend fun getAllCityNames(): AppResult<ImmutableList<String>, DataError> {
        val userRemoteId = encryptedPreferencesDataSource.readPreferences()
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { prefsFlow -> prefsFlow.firstOrNull()?.remoteId ?: -1 },
            )

        val cityNames = databaseDataSource.getAllCityNames(userId = userRemoteId)
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { it },
            )
            .toPersistentList()

        return AppResult.Success(data = cityNames)
    }

    override suspend fun getPreferredLeadsOrderTypeId(): AppResult<Int, DataError> {
        val preferredLeadsOrderTypeId = plainPreferencesDataSource.readPreferences()
            .fold(
                onError = { error -> return AppResult.Error(error = error) },
                onSuccess = { prefsFlow -> prefsFlow.firstOrNull()?.leadsOrderTypeId ?: 1 }
            )

        return AppResult.Success(data = preferredLeadsOrderTypeId)
    }

    override suspend fun getPreferredTenantsOrderTypeId(): AppResult<Int, DataError> {
        val preferredTenantsOrderTypeId = plainPreferencesDataSource.readPreferences()
            .fold(
                onError = { error -> return AppResult.Error(error = error) },
                onSuccess = { prefsFlow -> prefsFlow.firstOrNull()?.tenantsOrderTypeId ?: 1 }
            )

        return AppResult.Success(data = preferredTenantsOrderTypeId)
    }

    override suspend fun updatePreferredLeadsOrderTypeId(
        orderTypeId: Int,
    ): AppResult<Unit, DataError> {
        plainPreferencesDataSource.savePreferences { prefs ->
            prefs.copy(leadsOrderTypeId = orderTypeId)
        }
            .onError { return AppResult.Error(error = it) }

        return AppResult.Success(data = Unit)
    }

    override suspend fun updatePreferredTenantsOrderTypeId(
        orderTypeId: Int,
    ): AppResult<Unit, DataError> {
        plainPreferencesDataSource.savePreferences { prefs ->
            prefs.copy(tenantsOrderTypeId = orderTypeId)
        }
            .onError { return AppResult.Error(error = it) }

        return AppResult.Success(data = Unit)
    }

    override fun watchById(
        tenantId: Long,
    ): Flow<AppResult<Tenant?, DataError>> = flow {
        val tenantFlow = databaseDataSource.watchById(id = tenantId)
            .fold(
                onError = { return@flow emit(AppResult.Error(error = it)) },
                onSuccess = { it },
            )
            .map { tenant ->
                val domain = tenant?.toDomain()

                AppResult.Success<Tenant?, DataError>(domain)
            }
            .distinctUntilChanged()

        emitAll(tenantFlow)
    }

    override fun watchAll(
        tenantStatusFilter: TenantStatus?,
        matching: String?,
        orderTypeId: Int?,
    ): Flow<AppResult<ImmutableList<Tenant>, DataError>> =
        flow {
            val userRemoteId = encryptedPreferencesDataSource.readPreferences()
                .fold(
                    onError = {
                        emit(AppResult.Error(error = it))
                        return@flow
                    },
                    onSuccess = { prefsFlow -> prefsFlow.firstOrNull()?.remoteId ?: -1 },
                )

            val tenantsFlow = databaseDataSource.watchAll(
                userId = userRemoteId,
                tenantStatusIdFilter = tenantStatusFilter?.id,
                matching = matching,
                orderTypeId = orderTypeId,
            )
                .fold(
                    onError = { return@flow emit(AppResult.Error(error = it)) },
                    onSuccess = { it },
                )
                .map {
                    val tenants = it
                        .map { tenant -> tenant.toDomain() }
                        .toPersistentList()
                    AppResult.Success<ImmutableList<Tenant>, DataError>(data = tenants)
                }
                .distinctUntilChanged()

            emitAll(tenantsFlow)
        }

    override suspend fun isThereTenantWithName(
        tenantName: String,
        currentStoredTenantName: String?,
    ): AppResult<Boolean, DataError> {
        val userRemoteId = encryptedPreferencesDataSource.readPreferences()
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { prefsFlow -> prefsFlow.firstOrNull()?.remoteId ?: -1 },
            )

        val count = databaseDataSource.countTenantsWithName(
            userId = userRemoteId,
            tenantName = tenantName.normalizeEncoding(),
            currentStoredTenantName = currentStoredTenantName,
        )
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { count -> count },
            )

        return AppResult.Success(data = count > 0)
    }
}
