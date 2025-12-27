package com.fstengineering.leasilycrm.core.domain.useCases

import com.fstengineering.leasilycrm.core.domain.models.Property
import com.fstengineering.leasilycrm.core.domain.models.Stay
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transformLatest

class WatchPropertyWithStaysTenantsUseCaseImpl(
    private val propertyRepository: PropertyRepository,
    private val tenantRepository: TenantRepository,
) : WatchPropertyWithStaysTenantsUseCase {
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
                    val tenant = tenantRepository.getById(tenantId)
                        .fold(
                            onError = { stay.tenant },
                            onSuccess = { tenant -> tenant },
                        )

                    stay.copy(tenant = tenant)
                }
            }
            .awaitAll()
            .toPersistentList()
    }
}
