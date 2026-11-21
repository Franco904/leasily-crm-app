package com.fstengineering.leasilycrm.occupiedPeriodRegistration.infra.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.fold
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.dtos.TenantReadModel
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.repositories.TenantQueryRepository
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.infra.dataSources.ClienteReadDataSource
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

class TenantQueryRepositoryImpl(
    private val clienteReadDataSource: ClienteReadDataSource,
) : TenantQueryRepository {
    override suspend fun getById(tenantId: Long): AppResult<TenantReadModel> {
        return clienteReadDataSource.getCliente(clienteId = tenantId)
    }

    override suspend fun getAllWithoutUpcomingStays(
        matching: String?,
        currentTenantId: Long?,
    ): AppResult<ImmutableList<TenantReadModel>> {
        return clienteReadDataSource.getClientesWithoutEstadiaAtiva(
            matching = matching,
            currentClienteId = currentTenantId,
        ).fold(
            onError = { AppResult.Error(error = it) },
            onSuccess = { AppResult.Success(data = it.toPersistentList()) },
        )
    }
}
