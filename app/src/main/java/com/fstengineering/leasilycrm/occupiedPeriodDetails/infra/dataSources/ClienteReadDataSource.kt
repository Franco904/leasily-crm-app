package com.fstengineering.leasilycrm.occupiedPeriodDetails.infra.dataSources

import androidx.room.Dao
import androidx.room.Query
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.infra.utils.onDatabase
import com.fstengineering.leasilycrm.occupiedPeriodDetails.application.models.dtos.TenantReadModel
import com.fstengineering.leasilycrm.occupiedPeriodDetails.infra.projections.ClienteProjection

/** Leitura do Cliente associado ao Período Ocupado exibido nos detalhes. */
@Dao
abstract class ClienteReadDataSource {
    /** Busca o Cliente não excluído. */
    suspend fun getCliente(clienteId: Long): AppResult<TenantReadModel, ApplicationError> =
        onDatabase {
            val projection = findCliente(clienteId)
            TenantReadModel(id = projection.id, name = projection.name, city = projection.city)
        }

    @Query("SELECT id, name, city FROM lead_tenants WHERE id = :clienteId AND is_deleted = 0")
    protected abstract suspend fun findCliente(clienteId: Long): ClienteProjection
}
