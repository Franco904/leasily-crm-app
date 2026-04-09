package com.fstengineering.leasilycrm.core.application.useCases

import com.fstengineering.leasilycrm.core.application.repositories.LeadTenantSharedCommandRepository
import com.fstengineering.leasilycrm.core.application.repositories.PropertySharedCommandRepository
import com.fstengineering.leasilycrm.core.application.useCases.interfaces.WatchPropertyWithStaysTenantsUseCase
import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.entities.Property
import com.fstengineering.leasilycrm.core.domain.entities.Stay
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.utils.onSuccess
import com.fstengineering.leasilycrm.core.infra.utils.InfraError
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.transformLatest

class WatchPropertyWithStaysTenantsUseCaseImpl(
    private val propertySharedCommandRepository: PropertySharedCommandRepository,
    private val leadTenantSharedCommandRepository: LeadTenantSharedCommandRepository,
) : WatchPropertyWithStaysTenantsUseCase {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun invoke(
        propertyId: Long,
    ): Flow<AppResult<Property?, ApplicationError>> {
        return propertySharedCommandRepository.watchById(propertyId)
            .mapLatest { result ->
                result
                    .onError { error -> return@mapLatest AppResult.Error(error) }
                    .onSuccess { storedProperty ->
                        if (storedProperty == null) {
                            return@mapLatest AppResult.Success(data = null)
                        }

                        val ownedStays = storedProperty.stays
                            .filterIsInstance<Stay.Owned>()
                            .toImmutableList()
                        val updatedStays = resolveTenantsForStays(currentStays = ownedStays)

                        val newProperty = storedProperty.copy(stays = updatedStays)
                        return@mapLatest AppResult.Success(data = newProperty)
                    }
            }
    }

    private suspend fun resolveTenantsForStays(
        currentStays: ImmutableList<Stay.Owned>,
    ): ImmutableList<Stay.Owned> = coroutineScope {
        currentStays
            .map { stay ->
                async {
                    val tenantId = stay.tenant.tenantId
                    val tenant = leadTenantSharedCommandRepository.getById(tenantId)
                        .fold(
                            onError = { stay.tenant },
                            onSuccess = { tenant -> StayOwnedTenant.WithData(tenant) },
                        )

                    stay.putTenant(tenant = tenant)
                }
            }
            .awaitAll()
            .toPersistentList()
    }
}
