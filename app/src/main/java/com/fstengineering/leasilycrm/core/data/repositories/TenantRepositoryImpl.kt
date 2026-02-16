package com.fstengineering.leasilycrm.core.data.repositories

import com.fstengineering.leasilycrm.core.infra.adapters.interfaces.DatabaseAdapter
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.TenantEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.TenantDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.encrypted.EncryptedPreferences
import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.interfaces.PreferencesDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.plain.PlainPreferences
import com.fstengineering.leasilycrm.core.domain.entities.Tenant
import com.fstengineering.leasilycrm.core.domain.valueObjects.TenantStatus
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
        val tenantEntity = TenantEntity.fromDomain(tenant)

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
        val leads = databaseDataSource.getAllLeadsMatching(
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
        val tenants = databaseDataSource.getAllTenantsMatching(
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
        val tenants = databaseDataSource.getAllWithoutUpcomingStaysMatching(
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

    override suspend fun getAllTagNames(): AppResult<ImmutableList<String>, DataError> {
        return databaseDataSource.getAllTagNames()
            .fold(
                onError = { AppResult.Error(error = it) },
                onSuccess = { AppResult.Success(data = it.toPersistentList()) },
            )
    }

    override suspend fun getAllCityNames(): AppResult<ImmutableList<String>, DataError> {
        return databaseDataSource.getAllCityNames()
            .fold(
                onError = { AppResult.Error(error = it) },
                onSuccess = { AppResult.Success(data = it.toPersistentList()) },
            )
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
            val tenantsFlow = databaseDataSource.watchAll(
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
        val count = databaseDataSource.countTenantsWithName(
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
