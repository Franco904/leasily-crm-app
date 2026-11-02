package com.fstengineering.leasilycrm.proprietarioRegistration.infra.dataSources

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ProprietarioFileOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ProprietarioOrm
import com.fstengineering.leasilycrm.core.infra.utils.onDatabase
import com.fstengineering.leasilycrm.proprietarioRegistration.domain.entities.Proprietario
import com.fstengineering.leasilycrm.proprietarioRegistration.infra.mappers.toProprietarioFileOrm
import com.fstengineering.leasilycrm.proprietarioRegistration.infra.mappers.toProprietarioOrm

/** Escrita do cadastro de Proprietário. */
@Dao
abstract class ProprietarioWriteDataSource {
    /**
     * Salva o Proprietário com seus arquivos numa única transação. Com `id` preenchido, atualiza o
     * cadastro existente em vez de criar outro.
     */
    suspend fun save(proprietario: Proprietario): AppResult<Unit, ApplicationError> =
        onDatabase { saveInTransaction(proprietario) }

    /** Exclui o Proprietário, que deixa de aparecer na listagem e nos detalhes. */
    suspend fun delete(proprietarioId: Long): AppResult<Unit, ApplicationError> =
        onDatabase { markAsDeleted(proprietarioId) }

    @Transaction
    protected open suspend fun saveInTransaction(proprietario: Proprietario) {
        val proprietarioId = upsert(proprietario.toProprietarioOrm())
            .let { generatedId -> proprietario.id ?: generatedId }

        proprietario.files.forEach {
            upsert(it.toProprietarioFileOrm(proprietarioId))
        }
    }

    @Query(
        """
        UPDATE property_owners
        SET is_deleted = 1
        WHERE id = :proprietarioId
        """
    )
    protected abstract suspend fun markAsDeleted(proprietarioId: Long)

    @Upsert
    protected abstract suspend fun upsert(proprietario: ProprietarioOrm): Long

    @Upsert
    protected abstract suspend fun upsert(proprietarioFile: ProprietarioFileOrm): Long
}
