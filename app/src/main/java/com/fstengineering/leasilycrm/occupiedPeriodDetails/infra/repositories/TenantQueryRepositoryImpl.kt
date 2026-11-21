package com.fstengineering.leasilycrm.occupiedPeriodDetails.infra.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.occupiedPeriodDetails.application.models.dtos.TenantReadModel
import com.fstengineering.leasilycrm.occupiedPeriodDetails.application.repositories.TenantQueryRepository
import com.fstengineering.leasilycrm.occupiedPeriodDetails.infra.dataSources.ClienteReadDataSource

class TenantQueryRepositoryImpl(
    private val clienteReadDataSource: ClienteReadDataSource,
) : TenantQueryRepository {
    override suspend fun getById(tenantId: Long): AppResult<TenantReadModel> {
        return clienteReadDataSource.getCliente(clienteId = tenantId)
    }
}
