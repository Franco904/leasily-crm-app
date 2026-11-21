package com.fstengineering.leasilycrm.assistanceRegistration.infra.dataSources

import androidx.room.Dao
import androidx.room.Query
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.infra.utils.onDatabase

/** Escrita de Atendimento. */
@Dao
abstract class AssistanceWriteDataSource {
    /** Exclui o Atendimento, que deixa de contar como procura ativa do Cliente. */
    suspend fun delete(assistanceId: Long): AppResult<Unit> =
        onDatabase { markAsDeleted(assistanceId) }

    @Query("UPDATE assistances SET is_deleted = 1, is_finished = 1 WHERE id = :assistanceId")
    protected abstract suspend fun markAsDeleted(assistanceId: Long)
}
