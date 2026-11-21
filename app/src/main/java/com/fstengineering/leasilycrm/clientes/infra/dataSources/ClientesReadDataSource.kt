package com.fstengineering.leasilycrm.clientes.infra.dataSources

import androidx.room.Dao
import androidx.room.Query
import com.fstengineering.leasilycrm.clientes.application.models.ClienteReadModel
import com.fstengineering.leasilycrm.clientes.infra.mappers.toReadModel
import com.fstengineering.leasilycrm.clientes.infra.projections.ClienteListItemProjection
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.valueObjects.ClienteStatus
import com.fstengineering.leasilycrm.core.infra.utils.asDatabaseFlowResult
import com.fstengineering.leasilycrm.core.infra.utils.escapeLike
import com.fstengineering.leasilycrm.core.infra.utils.onDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val SELECT_CLIENTES = """
            SELECT
                c.id AS id,
                c.name AS name,
                c.city AS city,
                c.has_unfinished_assistance AS hasUnfinishedAssistance,
                (
                    SELECT a.adults + a.kids + a.babies
                    FROM assistances a
                    WHERE a.lead_tenant_id = c.id AND a.is_deleted = 0 AND a.is_finished = 0
                    LIMIT 1
                ) AS hospedesCount,
                c.has_upcoming_check_out AS hasUnfinishedStay,
                (
                    SELECT s.id
                    FROM stays s
                    WHERE s.tenant_id = c.id AND s.is_deleted = 0 AND (s.status = 0 OR s.status = 1)
                    LIMIT 1
                ) AS currentStayId
            FROM lead_tenants_read_view c
            WHERE c.is_deleted = 0
            AND (:statusId IS NULL OR c.status = :statusId)
            AND (
                :matching IS NULL OR
                c.name LIKE '%' || :matching || '%' ESCAPE '\' COLLATE NOCASE OR
                c.city LIKE '%' || :matching || '%' ESCAPE '\' COLLATE NOCASE
            )
        """
private const val ORDER_CLIENTES = """
            ORDER BY
                CASE WHEN :orderTypeId = 1 OR :orderTypeId IS NULL THEN c.name END COLLATE NOCASE,
                CASE WHEN :orderTypeId = 2 THEN c.update_date_time END DESC
        """

/** Leitura da listagem de Clientes, segmentada por status (Lead, Hóspede e Hóspede antigo). */
@Dao
abstract class ClientesReadDataSource {
    /**
     * Observa os Clientes não excluídos no [status] informado (ou de todos os estados, se `null`)
     * cujo nome ou cidade contém [matching], sem diferenciar maiúsculas de minúsculas. Sem
     * [matching], não filtra por texto. A ordem é por nome, ou pela última atualização, mais
     * recente primeiro, quando [orderTypeId] é 2.
     */
    fun watchClientes(
        status: ClienteStatus? = null,
        matching: String? = null,
        orderTypeId: Int? = null,
    ): Flow<AppResult<List<ClienteReadModel>>> =
        observeClientes(status?.id, matching?.escapeLike(), orderTypeId)
            .map { projections -> projections.map { it.toReadModel() } }
            .asDatabaseFlowResult()

    /** Lista os Leads com o mesmo filtro e ordem de [watchClientes], sem observar mudanças. */
    suspend fun getLeads(
        matching: String?,
        orderTypeId: Int,
    ): AppResult<List<ClienteReadModel>> = onDatabase {
        selectClientes(ClienteStatus.Lead.id, matching?.escapeLike(), orderTypeId)
            .map { it.toReadModel() }
    }

    @Query("$SELECT_CLIENTES $ORDER_CLIENTES")
    protected abstract fun observeClientes(
        statusId: Int?,
        matching: String?,
        orderTypeId: Int?,
    ): Flow<List<ClienteListItemProjection>>

    @Query("$SELECT_CLIENTES $ORDER_CLIENTES")
    protected abstract suspend fun selectClientes(
        statusId: Int?,
        matching: String?,
        orderTypeId: Int?,
    ): List<ClienteListItemProjection>
}
