package com.fstengineering.leasilycrm.clienteRegistration.infra.dataSources

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.fstengineering.leasilycrm.clienteRegistration.domain.entities.Cliente
import com.fstengineering.leasilycrm.clienteRegistration.infra.mappers.toClienteOrm
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ClienteOrm
import com.fstengineering.leasilycrm.core.infra.utils.onDatabase

/** Escrita do cadastro de Cliente. */
@Dao
abstract class ClienteWriteDataSource {
    /** Salva o Cliente. Com `id` preenchido, atualiza o cadastro existente em vez de criar outro. */
    suspend fun save(cliente: Cliente): AppResult<Unit, ApplicationError> =
        onDatabase {
            upsert(cliente.toClienteOrm())
        }

    /** Exclui o Cliente, que deixa de aparecer nas listagens e nos detalhes. */
    suspend fun delete(clienteId: Long): AppResult<Unit, ApplicationError> =
        onDatabase { markAsDeleted(clienteId) }

    @Upsert
    protected abstract suspend fun upsert(cliente: ClienteOrm): Long

    @Query("UPDATE lead_tenants SET is_deleted = 1 WHERE id = :clienteId")
    protected abstract suspend fun markAsDeleted(clienteId: Long)
}
