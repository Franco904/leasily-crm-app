package com.fstengineering.leasilycrm.atendimentoRegistration.infra.dataSources

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.fstengineering.leasilycrm.atendimentoRegistration.domain.entities.Atendimento
import com.fstengineering.leasilycrm.atendimentoRegistration.infra.mappers.toAtendimentoOrm
import com.fstengineering.leasilycrm.atendimentoRegistration.infra.mappers.toInteracaoOrm
import com.fstengineering.leasilycrm.core.domain.providers.interfaces.AppDateTimeProvider
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AtendimentoImovelCharacteristicCrossRefOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AtendimentoImovelEnviadoCrossRefOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AtendimentoOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AtendimentoPreferredNeighborhoodOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.InteracaoOrm
import com.fstengineering.leasilycrm.core.infra.utils.onDatabase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/** Escrita de Atendimento. */
@Dao
abstract class AtendimentoWriteDataSource : KoinComponent {
    private val dateTimeProvider: AppDateTimeProvider by inject()

    /**
     * Salva o Atendimento com Preferências de procura, Interações e Imóveis enviados numa única
     * transação: se o salvamento de algum deles falha, nada é salvo.
     */
    suspend fun save(atendimento: Atendimento): AppResult<Unit> =
        onDatabase { saveInTransaction(atendimento) }

    /** Exclui o Atendimento, que deixa de contar como procura ativa do Cliente. */
    suspend fun delete(atendimentoId: Long): AppResult<Unit> =
        onDatabase { markAsDeleted(atendimentoId) }

    @Transaction
    protected open suspend fun saveInTransaction(atendimento: Atendimento) {
        val atendimentoOrm = atendimento.toAtendimentoOrm(now = dateTimeProvider.now())
        val atendimentoId =
            upsert(atendimentoOrm).let { generatedId -> atendimentoOrm.id ?: generatedId }

        atendimento.preferredImovelCharacteristics.forEach {
            upsert(AtendimentoImovelCharacteristicCrossRefOrm(atendimentoId, it.id))
        }

        atendimento.interacoes.forEach { upsert(it.toInteracaoOrm(atendimentoId)) }

        atendimento.preferredNeighborhoods.forEach {
            upsert(
                AtendimentoPreferredNeighborhoodOrm(
                    neighborhood = it,
                    atendimentoId = atendimentoId
                )
            )
        }

        atendimento.imoveisEnviadosIds.forEach {
            upsert(
                AtendimentoImovelEnviadoCrossRefOrm(
                    atendimentoId = atendimentoId,
                    imovelId = it
                )
            )
        }
    }

    @Upsert
    protected abstract suspend fun upsert(atendimento: AtendimentoOrm): Long

    @Upsert
    protected abstract suspend fun upsert(crossRef: AtendimentoImovelCharacteristicCrossRefOrm): Long

    @Upsert
    protected abstract suspend fun upsert(interacao: InteracaoOrm): Long

    @Upsert
    protected abstract suspend fun upsert(neighborhood: AtendimentoPreferredNeighborhoodOrm): Long

    @Upsert
    protected abstract suspend fun upsert(crossRef: AtendimentoImovelEnviadoCrossRefOrm): Long

    @Query("UPDATE assistances SET is_deleted = 1, is_finished = 1 WHERE id = :atendimentoId")
    protected abstract suspend fun markAsDeleted(atendimentoId: Long)
}
