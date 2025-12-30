package com.fstengineering.leasilycrm.core.data.repositories

import com.fstengineering.leasilycrm.core.data.dataSources.database.daos.TenantDao
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.TenantEntity
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.encrypted.EncryptedPreferences
import com.fstengineering.leasilycrm.core.data.dataSources.preferences.interfaces.PreferencesStorage
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
    ): AppResult<Unit, DataError> {
        val userRemoteId = encryptedPreferencesStorage.readPreferences()
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { prefsFlow -> prefsFlow.first().remoteId ?: -1 },
            )

        val tenantWithUser = tenant.copy(userId = userRemoteId)
        val tenantEntity = TenantEntity.fromDomain(tenantWithUser)

        dao.insertOrUpdate(tenantEntity)

        return AppResult.Success(data = Unit)
    }

    override suspend fun delete(id: Long): AppResult<Unit, DataError> {
        dao.delete(id)

        return AppResult.Success(data = Unit)
    }

    override suspend fun getById(
        tenantId: Long,
    ): AppResult<Tenant, DataError> {
        val tenant = dao.getById(id = tenantId)
            ?.toDomain()

        return tenant?.let { AppResult.Success(data = tenant) }
            ?: AppResult.Error(DataError.UnknownError)
    }

    override suspend fun getAllLeadsMatching(
        matching: String?,
        orderTypeId: Int,
    ): AppResult<ImmutableList<Tenant>, DataError> {
        val userRemoteId = encryptedPreferencesStorage.readPreferences()
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { prefsFlow -> prefsFlow.first().remoteId ?: -1 },
            )

        val leads = dao.getAllLeadsMatching(
            userId = userRemoteId,
            matching = matching,
            order = orderTypeId,
        )
            .map { lead -> lead.toDomain() }
            .toPersistentList()

        return AppResult.Success(data = leads)
    }

    override suspend fun getAllTenantsMatching(
        matching: String?,
        orderTypeId: Int,
    ): AppResult<ImmutableList<Tenant>, DataError> {
        val userRemoteId = encryptedPreferencesStorage.readPreferences()
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { prefsFlow -> prefsFlow.first().remoteId ?: -1 },
            )

        val tenants = dao.getAllTenantsMatching(
            userId = userRemoteId,
            matching = matching,
            order = orderTypeId,
        )
            .map { tenant -> tenant.toDomain() }
            .toPersistentList()

        return AppResult.Success(data = tenants)
    }

    override suspend fun getAllWithoutUpcomingStaysMatching(
        matching: String?,
        currentTenantId: Long?,
    ): AppResult<ImmutableList<Tenant>, DataError> {
        val userRemoteId = encryptedPreferencesStorage.readPreferences()
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { prefsFlow -> prefsFlow.first().remoteId ?: -1 },
            )

        val tenants = dao.getAllWithoutUpcomingStaysMatching(
            userId = userRemoteId,
            matching = matching,
            currentTenantId = currentTenantId,
        )
            .map { tenant -> tenant.toDomain() }
            .toPersistentList()

        return AppResult.Success(data = tenants)
    }

    override suspend fun getAllCityNames(): AppResult<ImmutableList<String>, DataError> {
        val userRemoteId = encryptedPreferencesStorage.readPreferences()
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { prefsFlow -> prefsFlow.first().remoteId ?: -1 },
            )

        val cityNames =
            dao.getAllCityNames(userId = userRemoteId).toPersistentList()

        return AppResult.Success(data = cityNames)
    }

    override suspend fun getPreferredLeadsOrderTypeId(): AppResult<Int, DataError> {
        val preferredLeadsOrderTypeId = plainPreferencesStorage.readPreferences()
            .fold(
                onError = { error -> return AppResult.Error(error = error) },
                onSuccess = { prefsFlow -> prefsFlow.first().leadsOrderTypeId ?: 1 }
            )

        return AppResult.Success(data = preferredLeadsOrderTypeId)
    }

    override suspend fun getPreferredTenantsOrderTypeId(): AppResult<Int, DataError> {
        val preferredTenantsOrderTypeId = plainPreferencesStorage.readPreferences()
            .fold(
                onError = { error -> return AppResult.Error(error = error) },
                onSuccess = { prefsFlow -> prefsFlow.first().tenantsOrderTypeId ?: 1 }
            )

        return AppResult.Success(data = preferredTenantsOrderTypeId)
    }

    override suspend fun updatePreferredLeadsOrderTypeId(
        orderTypeId: Int,
    ): AppResult<Unit, DataError> {
        plainPreferencesStorage.savePreferences { prefs ->
            prefs.copy(leadsOrderTypeId = orderTypeId)
        }
            .onError { return AppResult.Error(error = it) }

        return AppResult.Success(data = Unit)
    }

    override suspend fun updatePreferredTenantsOrderTypeId(
        orderTypeId: Int,
    ): AppResult<Unit, DataError> {
        plainPreferencesStorage.savePreferences { prefs ->
            prefs.copy(tenantsOrderTypeId = orderTypeId)
        }
            .onError { return AppResult.Error(error = it) }

        return AppResult.Success(data = Unit)
    }

    override fun watchById(
        tenantId: Long,
    ): Flow<AppResult<Tenant?, DataError>> = flow {
        val tenantFlow = dao.watchById(id = tenantId)
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
            val userRemoteId = encryptedPreferencesStorage.readPreferences()
                .fold(
                    onError = {
                        emit(AppResult.Error(error = it))
                        return@flow
                    },
                    onSuccess = { prefsFlow -> prefsFlow.first().remoteId ?: -1 },
                )

            val tenantsFlow = dao.watchAll(
                userId = userRemoteId,
                tenantStatusIdFilter = tenantStatusFilter?.id,
                matching = matching,
                orderTypeId = orderTypeId,
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
        val userRemoteId = encryptedPreferencesStorage.readPreferences()
            .fold(
                onError = { return AppResult.Error(error = it) },
                onSuccess = { prefsFlow -> prefsFlow.first().remoteId ?: -1 },
            )

        val count = dao.countTenantsWithName(
            userId = userRemoteId,
            tenantName = tenantName.normalizeEncoding(),
            currentStoredTenantName = currentStoredTenantName,
        )

        return AppResult.Success(data = count > 0)
    }
}
