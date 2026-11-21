package com.fstengineering.leasilycrm.clienteDetails.infra.dataSources

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.fstengineering.leasilycrm.clienteDetails.application.models.ClienteReadModel
import com.fstengineering.leasilycrm.clienteDetails.infra.mappers.toReadModel
import com.fstengineering.leasilycrm.clienteDetails.infra.projections.ClienteDetailsProjection
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.infra.utils.asDatabaseFlowResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Leitura dos detalhes do Cliente (SCV): contato, Atendimentos com Imóveis enviados e Estadias. */
@Dao
abstract class ClienteDetailsReadDataSource {
    /**
     * Observa o Cliente não excluído com seus Atendimentos, Interações, Imóveis enviados e
     * Estadias, lidos numa única transação, e emite de novo quando qualquer um deles muda. Emite
     * `null` quando o Cliente não existe ou foi excluído.
     */
    fun watchCliente(clienteId: Long): Flow<AppResult<ClienteReadModel?, ApplicationError>> =
        observeCliente(clienteId)
            .map { projection -> projection?.toReadModel() }
            .asDatabaseFlowResult()

    @Transaction
    @Query(
        """
        SELECT id, tag, name, city, identity_document, phone, email, contact_origin, observations,
            status
        FROM lead_tenants_read_view
        WHERE id = :clienteId AND is_deleted = 0
        """
    )
    protected abstract fun observeCliente(clienteId: Long): Flow<ClienteDetailsProjection?>
}
