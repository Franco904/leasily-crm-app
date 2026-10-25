package com.fstengineering.leasilycrm.proprietarioRegistration.infra.dataSources

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.proprietarioRegistration.infra.projections.ProprietarioRegistrationProjection
import com.fstengineering.leasilycrm.core.infra.utils.onDatabase
import com.fstengineering.leasilycrm.proprietarioRegistration.application.models.ProprietarioReadModel
import com.fstengineering.leasilycrm.proprietarioRegistration.infra.mappers.toProprietarioReadModel

/** Leitura do Proprietário a editar no cadastro. */
@Dao
abstract class ProprietarioRegistrationReadDataSource {
    /** Busca o Proprietário com seus arquivos, para preencher o formulário de edição. */
    suspend fun getProprietario(proprietarioId: Long): AppResult<ProprietarioReadModel, ApplicationError> =
        onDatabase { findProprietario(proprietarioId).toProprietarioReadModel() }

    @Transaction
    @Query(
        """
        SELECT id, create_date_time, update_date_time, name, phone, email, payment_key
        FROM property_owners
        WHERE id = :proprietarioId AND is_deleted = 0
        """
    )
    protected abstract suspend fun findProprietario(proprietarioId: Long): ProprietarioRegistrationProjection
}
