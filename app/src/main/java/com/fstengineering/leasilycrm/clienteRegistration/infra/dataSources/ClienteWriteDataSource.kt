package com.fstengineering.leasilycrm.clienteRegistration.infra.dataSources

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.fstengineering.leasilycrm.clienteRegistration.domain.entities.Cliente
import com.fstengineering.leasilycrm.clienteRegistration.infra.mappers.toClienteOrm
import com.fstengineering.leasilycrm.core.domain.providers.interfaces.AppDateTimeProvider
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ClienteOrm
import com.fstengineering.leasilycrm.core.infra.utils.onDatabase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/** Escrita do cadastro de Cliente. */
@Dao
abstract class ClienteWriteDataSource : KoinComponent {
    private val dateTimeProvider: AppDateTimeProvider by inject()

    /** Salva o Cliente. Com `id` preenchido, atualiza o cadastro existente em vez de criar outro. */
    suspend fun save(cliente: Cliente): AppResult<Unit> =
        onDatabase {
            upsert(cliente.toClienteOrm(now = dateTimeProvider.now()))
        }

    /** Exclui o Cliente, que deixa de aparecer nas listagens e nos detalhes. */
    suspend fun delete(clienteId: Long): AppResult<Unit> =
        onDatabase { markAsDeleted(clienteId) }

    @Upsert
    protected abstract suspend fun upsert(cliente: ClienteOrm): Long

    @Query("UPDATE lead_tenants SET is_deleted = 1 WHERE id = :clienteId")
    protected abstract suspend fun markAsDeleted(clienteId: Long)
}
