package com.fstengineering.leasilycrm.occupiedPeriodRegistration.infra.dataSources

import androidx.room.Dao
import androidx.room.Query
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.infra.utils.escapeLike
import com.fstengineering.leasilycrm.core.infra.utils.onDatabase
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.dtos.TenantReadModel
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.infra.projections.ClienteProjection

/** Leitura dos Clientes selecionáveis como Hóspede no cadastro de Estadia. */
@Dao
abstract class ClienteReadDataSource {
    /** Busca o Cliente não excluído. */
    suspend fun getCliente(clienteId: Long): AppResult<TenantReadModel> =
        onDatabase { selectCliente(clienteId).toTenantReadModel() }

    /**
     * Lista, em ordem alfabética, os Clientes não excluídos sem Estadia ativa cujo nome ou cidade
     * contém [matching], sem diferenciar maiúsculas de minúsculas. O Cliente [currentClienteId]
     * entra na lista mesmo com Estadia ativa, para permitir editar a própria Estadia.
     */
    suspend fun getClientesWithoutEstadiaAtiva(
        matching: String?,
        currentClienteId: Long?,
    ): AppResult<List<TenantReadModel>> = onDatabase {
        selectClientesWithoutEstadiaAtiva(matching?.escapeLike(), currentClienteId)
            .map { it.toTenantReadModel() }
    }

    @Query("SELECT id, name, city FROM lead_tenants WHERE id = :clienteId AND is_deleted = 0")
    protected abstract suspend fun selectCliente(clienteId: Long): ClienteProjection

    @Query(
        """
        SELECT id, name, city
        FROM lead_tenants_read_view
        WHERE (status <> 1 OR (:currentClienteId IS NOT NULL AND id = :currentClienteId))
        AND is_deleted = 0
        AND (
            :matching IS NULL OR
            name LIKE '%' || :matching || '%' ESCAPE '\' COLLATE NOCASE OR
            city LIKE '%' || :matching || '%' ESCAPE '\' COLLATE NOCASE
        )
        ORDER BY name
        """
    )
    protected abstract suspend fun selectClientesWithoutEstadiaAtiva(
        matching: String?,
        currentClienteId: Long?,
    ): List<ClienteProjection>

    private fun ClienteProjection.toTenantReadModel() =
        TenantReadModel(id = id, name = name, city = city)
}
