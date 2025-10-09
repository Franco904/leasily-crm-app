package com.fstengineering.leasilycrm.core.data.repositories

import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.TenantDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.TenantEntity
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.encrypted.EncryptedPreferences
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.interfaces.PreferencesStorage
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.plain.PlainPreferences
import com.fstengineering.leasilycrm.core.domain.models.Tenant
import com.fstengineering.leasilycrm.core.domain.repositories.TenantRepository
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.presentation.utils.normalizeEncoding
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class TenantRepositoryImpl(
    private val dao: TenantDao,
    private val encryptedPreferencesStorage: PreferencesStorage<EncryptedPreferences>,
    private val plainPreferencesStorage: PreferencesStorage<PlainPreferences>,
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

        val tenantWithUser = tenant.copy(userId = userRemoteId)
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

    override suspend fun getAllLeadsMatching(
        matching: String?,
        orderTypeId: Int,
    ): AppResult<ImmutableList<Tenant>, DataSourceError> {
        val userRemoteId = encryptedPreferencesStorage.readPreferences()
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { prefsFlow ->
                    prefsFlow.first().remoteId ?: return AppResult.Error(
                        error = DataSourceError.PreferencesError.NotFound,
                    )
                }
            )

        val leads = dao.getAllLeadsMatching(
            userId = userRemoteId,
            matching = matching,
            order = orderTypeId,
        )
            .map { lead -> lead.toDomain() }.toPersistentList()

        return AppResult.Success(data = leads)
    }

    override suspend fun getAllTenantsMatching(
        matching: String?,
        orderTypeId: Int,
    ): AppResult<ImmutableList<Tenant>, DataSourceError> {
        val userRemoteId = encryptedPreferencesStorage.readPreferences()
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { prefsFlow ->
                    prefsFlow.first().remoteId ?: return AppResult.Error(
                        error = DataSourceError.PreferencesError.NotFound,
                    )
                }
            )

        val tenants = dao.getAllTenantsMatching(
            userId = userRemoteId,
            matching = matching,
            order = orderTypeId,
        )
            .map { tenant -> tenant.toDomain() }.toPersistentList()

        return AppResult.Success(data = tenants)
    }

    override suspend fun getAllWithoutUpcomingStaysMatching(
        matching: String?,
        currentTenantId: Long?,
    ): AppResult<ImmutableList<Tenant>, DataSourceError> {
        val userRemoteId = encryptedPreferencesStorage.readPreferences()
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { prefsFlow ->
                    prefsFlow.first().remoteId ?: return AppResult.Error(
                        error = DataSourceError.PreferencesError.NotFound,
                    )
                }
            )

        val tenants = dao.getAllWithoutUpcomingStaysMatching(
            userId = userRemoteId,
            matching = matching,
            currentTenantId = currentTenantId,
        )
            .map { tenant -> tenant.toDomain() }.toPersistentList()

        return AppResult.Success(data = tenants)
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

    override suspend fun getPreferredLeadsOrderTypeId(): AppResult<Int, DataSourceError> {
        val preferredLeadsOrderTypeId = plainPreferencesStorage.readPreferences()
            .fold(
                onError = { error -> return AppResult.Error(error = error) },
                onSuccess = { prefsFlow -> prefsFlow.first().leadsOrderTypeId ?: 1 }
            )

        return AppResult.Success(data = preferredLeadsOrderTypeId)
    }

    override suspend fun getPreferredTenantsOrderTypeId(): AppResult<Int, DataSourceError> {
        val preferredTenantsOrderTypeId = plainPreferencesStorage.readPreferences()
            .fold(
                onError = { error -> return AppResult.Error(error = error) },
                onSuccess = { prefsFlow -> prefsFlow.first().tenantsOrderTypeId ?: 1 }
            )

        return AppResult.Success(data = preferredTenantsOrderTypeId)
    }

    override suspend fun updatePreferredLeadsOrderTypeId(
        orderTypeId: Int,
    ): AppResult<Unit, DataSourceError> {
        plainPreferencesStorage.savePreferences { prefs ->
            prefs.copy(leadsOrderTypeId = orderTypeId)
        }
            .onError { return AppResult.Error(error = it) }

        return AppResult.Success(data = Unit)
    }

    override suspend fun updatePreferredTenantsOrderTypeId(
        orderTypeId: Int,
    ): AppResult<Unit, DataSourceError> {
        plainPreferencesStorage.savePreferences { prefs ->
            prefs.copy(tenantsOrderTypeId = orderTypeId)
        }
            .onError { return AppResult.Error(error = it) }

        return AppResult.Success(data = Unit)
    }

    override fun watchById(
        tenantId: Long,
    ): Flow<AppResult<Tenant?, DataSourceError>> = flow {
        val tenantFlow = dao.watchById(id = tenantId)
            .map { tenant ->
                val domain = tenant?.toDomain()

                AppResult.Success<Tenant?, DataSourceError>(domain)
            }
            .distinctUntilChanged()

        emitAll(tenantFlow)
    }

    override fun watchAll(): Flow<AppResult<ImmutableList<Tenant>, DataSourceError>> = flow {
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
                    val leads = it.map { tenant -> tenant.toDomain() }.toPersistentList()
                    AppResult.Success<ImmutableList<Tenant>, DataSourceError>(data = leads)
                }
                .distinctUntilChanged()
        )
    }

    override fun watchAllLeads(
        orderTypeId: Int,
    ): Flow<AppResult<ImmutableList<Tenant>, DataSourceError>> =
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
                dao.watchAllLeads(userId = userRemoteId, order = orderTypeId)
                    .map {
                        val leads = it.map { tenant -> tenant.toDomain() }.toPersistentList()
                        AppResult.Success<ImmutableList<Tenant>, DataSourceError>(data = leads)
                    }
                    .distinctUntilChanged()
            )
        }

    override fun watchAllTenantsMatching(
        orderTypeId: Int,
        tenantFilterType: Int?,
        matching: String?,
    ): Flow<AppResult<ImmutableList<Tenant>, DataSourceError>> =
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

            val tenantsFlow = dao.watchAllTenants(
                userId = userRemoteId,
                order = orderTypeId,
                tenantFilterType = tenantFilterType,
                matching = matching,
            )
                .map {
                    val tenants = it.map { tenant -> tenant.toDomain() }.toPersistentList()
                    AppResult.Success<ImmutableList<Tenant>, DataSourceError>(data = tenants)
                }
                .distinctUntilChanged()

            emitAll(tenantsFlow)
        }

    override suspend fun isThereTenantWithName(
        tenantName: String,
        currentStoredTenantName: String?,
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

        val count = dao.countTenantsWithName(
            userId = userRemoteId,
            tenantName = tenantName.normalizeEncoding(),
            currentStoredTenantName = currentStoredTenantName,
        )

        return AppResult.Success(data = count > 0)
    }
}
