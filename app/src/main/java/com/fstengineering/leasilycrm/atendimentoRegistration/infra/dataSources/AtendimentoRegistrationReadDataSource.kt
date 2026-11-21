package com.fstengineering.leasilycrm.atendimentoRegistration.infra.dataSources

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.fstengineering.leasilycrm.atendimentoRegistration.application.models.AtendimentoReadModel
import com.fstengineering.leasilycrm.atendimentoRegistration.infra.mappers.toReadModel
import com.fstengineering.leasilycrm.atendimentoRegistration.infra.projections.AtendimentoRegistrationProjection
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.infra.utils.onDatabase

/** Leitura do cadastro de Atendimento: o Atendimento a editar e os bairros já procurados. */
@Dao
abstract class AtendimentoRegistrationReadDataSource {
    /**
     * Busca o Atendimento não excluído com o nome do Cliente, Preferências de procura, Interações
     * e Imóveis enviados não excluídos; falha quando o Atendimento não existe.
     */
    suspend fun getAtendimento(atendimentoId: Long): AppResult<AtendimentoReadModel> =
        onDatabase { requireNotNull(selectAtendimento(atendimentoId)).toReadModel() }

    /** Lista os bairros já procurados em Atendimentos, sem repetição e em ordem alfabética. */
    suspend fun getNeighborhoodNames(): AppResult<List<String>> =
        onDatabase { selectNeighborhoodNames() }

    @Transaction
    @Query("SELECT * FROM assistances WHERE id = :atendimentoId AND is_deleted = 0")
    protected abstract suspend fun selectAtendimento(
        atendimentoId: Long,
    ): AtendimentoRegistrationProjection?

    @Query(
        """
        SELECT DISTINCT neighborhood
        FROM assistance_preferred_neighborhoods apn
        INNER JOIN assistances a ON a.id = apn.assistance_id
        INNER JOIN lead_tenants_read_view lt ON lt.id = a.lead_tenant_id
        ORDER BY neighborhood
        """
    )
    protected abstract suspend fun selectNeighborhoodNames(): List<String>
}
