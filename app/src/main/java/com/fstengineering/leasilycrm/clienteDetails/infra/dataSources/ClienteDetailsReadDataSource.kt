package com.fstengineering.leasilycrm.clienteDetails.infra.dataSources

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.fstengineering.leasilycrm.clienteDetails.application.models.ClienteReadModel
import com.fstengineering.leasilycrm.clienteDetails.infra.mappers.toAssistanceReadModel
import com.fstengineering.leasilycrm.clienteDetails.infra.mappers.toAssistanceSentImovelReadModel
import com.fstengineering.leasilycrm.clienteDetails.infra.mappers.toClienteReadModel
import com.fstengineering.leasilycrm.clienteDetails.infra.mappers.toStayReadModel
import com.fstengineering.leasilycrm.clienteDetails.infra.projections.ClienteDetailsProjection
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.StayEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.AssistanceCompound
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.ImovelCompound
import com.fstengineering.leasilycrm.core.infra.utils.asDatabaseFlowResult
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Leitura dos detalhes do Cliente (SCV): contato, Atendimentos com Imóveis enviados e Estadias. */
@Dao
abstract class ClienteDetailsReadDataSource {
    /**
     * Observa o Cliente não excluído com seus Atendimentos, Imóveis enviados e Estadias, e emite
     * de novo quando muda o Cliente, seus Atendimentos ou suas Estadias. Emite `null` quando o Cliente não existe ou foi
     * excluído.
     */
    fun watchCliente(clienteId: Long): Flow<AppResult<ClienteReadModel?, ApplicationError>> =
        observeCliente(clienteId)
            .map { projection ->
                projection?.let { assembleClienteReadModel(it) }
            }
            .asDatabaseFlowResult()

    private suspend fun assembleClienteReadModel(
        projection: ClienteDetailsProjection,
    ): ClienteReadModel {
        val assistances = selectAssistances(projection.id)
            .map { compound ->
                val sentImoveis = selectSentImoveis(requireNotNull(compound.assistance.id))
                    .map { it.toAssistanceSentImovelReadModel() }
                    .toPersistentList()

                compound.toAssistanceReadModel(sentImoveis)
            }
            .toPersistentList()

        val stays = selectStays(projection.id)
            .map { it.toStayReadModel() }
            .toPersistentList()

        return projection.toClienteReadModel(assistances = assistances, stays = stays)
    }

    @Query(
        """
        SELECT id, tag, name, city, identity_document, phone, email, contact_origin, observations,
            status
        FROM lead_tenants_read_view
        WHERE id = :clienteId AND is_deleted = 0
        """
    )
    protected abstract fun observeCliente(clienteId: Long): Flow<ClienteDetailsProjection?>

    @Transaction
    @Query("SELECT * FROM assistances WHERE lead_tenant_id = :clienteId AND is_deleted = 0")
    protected abstract suspend fun selectAssistances(clienteId: Long): List<AssistanceCompound>

    @Transaction
    @Query(
        """
        SELECT * FROM properties
        INNER JOIN assistance_sent_properties asp ON properties.id = asp.property_id
        WHERE asp.assistance_id = :assistanceId
        AND is_deleted = 0
        """
    )
    protected abstract suspend fun selectSentImoveis(assistanceId: Long): List<ImovelCompound>

    @Query("SELECT * FROM stays WHERE tenant_id = :clienteId AND is_deleted = 0")
    protected abstract suspend fun selectStays(clienteId: Long): List<StayEntity>
}
