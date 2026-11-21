package com.fstengineering.leasilycrm.core.infra.dataSources.database

import androidx.room.Dao
import androidx.room.Query
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.infra.utils.onDatabase

/** Leitura de Cliente usada por mais de um contexto delimitado. */
@Dao
abstract class ClienteSharedReadDataSource {
    /** Busca o nome do Cliente não excluído. */
    suspend fun getName(clienteId: Long): AppResult<String> =
        onDatabase { selectName(clienteId) }

    @Query("SELECT name FROM lead_tenants WHERE id = :clienteId AND is_deleted = 0")
    protected abstract suspend fun selectName(clienteId: Long): String
}
