package com.fstengineering.leasilycrm.core.domain.useCases

import androidx.collection.LruCache
import com.fstengineering.leasilycrm.core.domain.models.Property
import com.fstengineering.leasilycrm.core.domain.models.Stay
import com.fstengineering.leasilycrm.core.domain.models.Tenant
import com.fstengineering.leasilycrm.core.domain.repositories.PropertyRepository
import com.fstengineering.leasilycrm.core.domain.repositories.TenantRepository
import com.fstengineering.leasilycrm.core.domain.useCases.interfaces.WatchPropertyWithStaysTenantsUseCase
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.launch

class WatchPropertyWithStaysTenantsUseCaseImpl(
    private val propertyRepository: PropertyRepository,
    private val tenantRepository: TenantRepository,
    private val applicationScope: CoroutineScope,
) : WatchPropertyWithStaysTenantsUseCase {
    val tenantCache = LruCache<Long, Tenant>(maxSize = 150)

    init {
        observeTenantChanges()
    }

    private fun observeTenantChanges() {
        applicationScope.launch {
            tenantRepository.watchAll()
                .collect { result ->
                    result
                        .onError { _ -> return@collect }
                        .onSuccess { updatedTenants ->
                            updatedTenants.forEach { tenant ->
                                if (tenant.id == null) return@forEach
                                tenantCache.remove(key = tenant.id)
                            }
                        }
                }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun invoke(
        propertyId: Long,
    ): Flow<AppResult<Property?, DataSourceError>> {
        return propertyRepository.watchById(propertyId)
            .transformLatest { result ->
                result
                    .onError { error -> emit(AppResult.Error(error)) }
                    .onSuccess { storedProperty ->
                        if (storedProperty == null) {
                            emit(AppResult.Success(data = null))
                            return@onSuccess
                        }

                        val updatedStays = resolveTenantsForStays(
                            currentStays = storedProperty.stays,
                        )

                        val newProperty = storedProperty.copy(stays = updatedStays)
                        emit(AppResult.Success(data = newProperty))
                    }
            }
    }

    private suspend fun resolveTenantsForStays(
        currentStays: ImmutableList<Stay>,
    ): ImmutableList<Stay> = coroutineScope {
        currentStays
            .map { stay ->
                async {
                    val tenantId = stay.tenant?.id ?: return@async stay

                    val tenant = tenantCache.get(key = tenantId) ?: run {
                        tenantRepository.getById(tenantId)
                            .fold(
                                onError = { stay.tenant },
                                onSuccess = { tenant ->
                                    tenant.also { tenantCache.put(key = tenantId, value = tenant) }
                                },
                            )
                    }

                    stay.copy(tenant = tenant)
                }
            }
            .awaitAll()
            .toPersistentList()
    }
}
