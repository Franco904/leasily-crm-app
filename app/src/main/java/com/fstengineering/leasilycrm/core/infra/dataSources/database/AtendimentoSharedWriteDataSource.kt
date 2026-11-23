package com.fstengineering.leasilycrm.core.infra.dataSources.database

import androidx.room.Dao
import androidx.room.Query
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.infra.utils.onDatabase

/**
 * Escrita de Atendimento usada por outros contextos delimitados: pelo check-out da Estadia e pela
 * exclusão de Atendimento nos detalhes do Cliente.
 */
@Dao
abstract class AtendimentoSharedWriteDataSource {
    /** Finaliza o Atendimento "Não finalizado" do Cliente, convertido em Estadia. */
    suspend fun finishOpen(clienteId: Long): AppResult<Unit> =
        onDatabase { markOpenAsFinished(clienteId) }

    /** Reabre o Atendimento do Cliente finalizado pelo check-out que está sendo desfeito. */
    suspend fun unfinish(clienteId: Long): AppResult<Unit> =
        onDatabase { markFinishedAsOpen(clienteId) }

    /** Exclui o Atendimento, que deixa de contar como procura ativa do Cliente. */
    suspend fun delete(atendimentoId: Long): AppResult<Unit> =
        onDatabase { markAsDeleted(atendimentoId) }

    @Query("UPDATE assistances SET is_deleted = 1, is_finished = 1 WHERE id = :atendimentoId")
    protected abstract suspend fun markAsDeleted(atendimentoId: Long)

    @Query(
        """
        UPDATE assistances
        SET is_finished = 1
        WHERE lead_tenant_id = :clienteId AND is_finished = 0 AND is_deleted = 0
        """
    )
    protected abstract suspend fun markOpenAsFinished(clienteId: Long)

    @Query(
        """
        UPDATE assistances
        SET is_finished = 0
        WHERE lead_tenant_id = :clienteId AND is_finished = 1 AND is_deleted = 0
        """
    )
    protected abstract suspend fun markFinishedAsOpen(clienteId: Long)
}
