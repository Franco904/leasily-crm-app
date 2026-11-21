package com.fstengineering.leasilycrm.clienteRegistration.infra.dataSources

import androidx.room.Dao
import androidx.room.Query
import com.fstengineering.leasilycrm.clienteRegistration.application.models.ClienteReadModel
import com.fstengineering.leasilycrm.clienteRegistration.infra.mappers.toReadModel
import com.fstengineering.leasilycrm.clienteRegistration.infra.projections.ClienteRegistrationProjection
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.infra.utils.onDatabase

/** Leitura do Cliente a editar e das sugestões de preenchimento do cadastro. */
@Dao
abstract class ClienteRegistrationReadDataSource {
    /** Busca o Cliente não excluído, para preencher o formulário de edição. */
    suspend fun getCliente(clienteId: Long): AppResult<ClienteReadModel, ApplicationError> =
        onDatabase { findCliente(clienteId).toReadModel() }

    /** Lista as etiquetas já usadas por Clientes não excluídos, em ordem alfabética. */
    suspend fun getTags(): AppResult<List<String>, ApplicationError> =
        onDatabase { selectTags() }

    /** Lista as cidades já usadas por Clientes não excluídos, em ordem alfabética. */
    suspend fun getCities(): AppResult<List<String>, ApplicationError> =
        onDatabase { selectCities() }

    /** Lista as Origens do contato já usadas por Clientes não excluídos, em ordem alfabética. */
    suspend fun getContactOrigins(): AppResult<List<String>, ApplicationError> =
        onDatabase { selectContactOrigins() }

    @Query(
        """
        SELECT id, create_date_time, tag, name, city, identity_document, phone, email,
            contact_origin, observations, status
        FROM lead_tenants_read_view
        WHERE id = :clienteId AND is_deleted = 0
        """
    )
    protected abstract suspend fun findCliente(clienteId: Long): ClienteRegistrationProjection

    @Query(
        """
        SELECT DISTINCT tag
        FROM lead_tenants
        WHERE tag IS NOT NULL AND is_deleted = 0
        ORDER BY tag
        """
    )
    protected abstract suspend fun selectTags(): List<String>

    @Query(
        """
        SELECT DISTINCT city
        FROM lead_tenants
        WHERE city IS NOT NULL AND is_deleted = 0
        ORDER BY city
        """
    )
    protected abstract suspend fun selectCities(): List<String>

    @Query(
        """
        SELECT DISTINCT contact_origin
        FROM lead_tenants
        WHERE contact_origin IS NOT NULL AND is_deleted = 0
        ORDER BY contact_origin
        """
    )
    protected abstract suspend fun selectContactOrigins(): List<String>
}
