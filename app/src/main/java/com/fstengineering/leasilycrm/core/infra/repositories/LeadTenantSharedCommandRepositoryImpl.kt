package com.fstengineering.leasilycrm.core.infra.repositories

import com.fstengineering.leasilycrm.core.application.repositories.LeadTenantSharedCommandRepository
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.entities.Tenant
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.valueObjects.TenantStatus
import com.fstengineering.leasilycrm.core.infra.adapters.interfaces.DatabaseAdapter
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.TenantDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.interfaces.PreferencesDataSource
import com.fstengineering.leasilycrm.core.infra.dataSources.preferences.plain.PlainPreferences
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class LeadTenantSharedCommandRepositoryImpl(
    private val databaseAdapter: DatabaseAdapter,
    private val databaseDataSource: TenantDatabaseDataSource,
    private val plainPreferencesDataSource: PreferencesDataSource<PlainPreferences>,
) : LeadTenantSharedCommandRepository {
    override suspend fun delete(id: Long): AppResult<Unit, ApplicationError> {
        return databaseAdapter.withTransaction {
            databaseDataSource.delete(id)
        }
            .fold(
                onError = { AppResult.Error(error = it.toApplicationError()) },
                onSuccess = { AppResult.Success(data = Unit) }
            )
    }

    override suspend fun getById(
        tenantId: Long,
    ): AppResult<Tenant, ApplicationError> {
        val tenant = databaseDataSource.getById(id = tenantId)
            .fold(
                onError = { return AppResult.Error(error = it.toApplicationError()) },
                onSuccess = { it?.toDomain() }
            )

        return tenant?.let { AppResult.Success(data = tenant) }
            ?: AppResult.Error(ApplicationError.Unknown)
    }

    override suspend fun getAllWithoutUpcomingStaysMatching(
        matching: String?,
        currentTenantId: Long?,
    ): AppResult<ImmutableList<Tenant>, ApplicationError> {
        val tenants = databaseDataSource.getAllWithoutUpcomingStaysMatching(
            matching = matching,
            currentTenantId = currentTenantId,
        )
            .fold(
                onError = { return AppResult.Error(error = it.toApplicationError()) },
                onSuccess = { it },
            )
            .map { tenant -> tenant.toDomain() }
            .toPersistentList()

        return AppResult.Success(data = tenants)
    }

    override suspend fun getPreferredLeadsOrderTypeId(): AppResult<Int, ApplicationError> {
        val preferredLeadsOrderTypeId = plainPreferencesDataSource.readPreferences()
            .fold(
                onError = { return AppResult.Error(error = it.toApplicationError()) },
                onSuccess = { prefsFlow -> prefsFlow.firstOrNull()?.leadsOrderTypeId ?: 1 }
            )

        return AppResult.Success(data = preferredLeadsOrderTypeId)
    }

    override suspend fun getPreferredTenantsOrderTypeId(): AppResult<Int, ApplicationError> {
        val preferredTenantsOrderTypeId = plainPreferencesDataSource.readPreferences()
            .fold(
                onError = { return AppResult.Error(error = it.toApplicationError()) },
                onSuccess = { prefsFlow -> prefsFlow.firstOrNull()?.tenantsOrderTypeId ?: 1 }
            )

        return AppResult.Success(data = preferredTenantsOrderTypeId)
    }

    override suspend fun updatePreferredLeadsOrderTypeId(
        orderTypeId: Int,
    ): AppResult<Unit, ApplicationError> {
        plainPreferencesDataSource.savePreferences { prefs ->
            prefs.copy(leadsOrderTypeId = orderTypeId)
        }
            .onError { return AppResult.Error(error = it.toApplicationError()) }

        return AppResult.Success(data = Unit)
    }

    override suspend fun updatePreferredTenantsOrderTypeId(
        orderTypeId: Int,
    ): AppResult<Unit, ApplicationError> {
        plainPreferencesDataSource.savePreferences { prefs ->
            prefs.copy(tenantsOrderTypeId = orderTypeId)
        }
            .onError { return AppResult.Error(error = it.toApplicationError()) }

        return AppResult.Success(data = Unit)
    }

    override fun watchById(
        tenantId: Long,
    ): Flow<AppResult<Tenant?, ApplicationError>> = flow {
        val tenantFlow = databaseDataSource.watchById(id = tenantId)
            .fold(
                onError = { return@flow emit(AppResult.Error(error = it.toApplicationError())) },
                onSuccess = { it },
            )
            .map { tenant ->
                val domain = tenant?.toDomain()

                AppResult.Success<Tenant?, ApplicationError>(domain)
            }
            .distinctUntilChanged()

        emitAll(tenantFlow)
    }

    override fun watchAll(
        tenantStatusFilter: TenantStatus?,
        matching: String?,
        orderTypeId: Int?,
    ): Flow<AppResult<ImmutableList<Tenant>, ApplicationError>> =
        flow {
            val tenantsFlow = databaseDataSource.watchAll(
                tenantStatusIdFilter = tenantStatusFilter?.id,
                matching = matching,
                orderTypeId = orderTypeId,
            )
                .fold(
                    onError = { return@flow emit(AppResult.Error(error = it.toApplicationError())) },
                    onSuccess = { it },
                )
                .map {
                    val tenants = it
                        .map { tenant -> tenant.toDomain() }
                        .toPersistentList()
                    AppResult.Success<ImmutableList<Tenant>, ApplicationError>(data = tenants)
                }
                .distinctUntilChanged()

            emitAll(tenantsFlow)
        }
}
