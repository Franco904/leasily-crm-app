package com.fstengineering.leasilycrm.proprietarioRegistration.infra.dataSources

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.ProprietarioCompound
import com.fstengineering.leasilycrm.core.infra.utils.onDatabase
import com.fstengineering.leasilycrm.proprietarioRegistration.application.models.dtos.ProprietarioReadModel
import com.fstengineering.leasilycrm.proprietarioRegistration.infra.mappers.toReadModel

/** Leitura do Proprietário a editar no cadastro. */
@Dao
abstract class ProprietarioRegistrationReadDataSource {
    /** Busca o Proprietário com seus arquivos, para preencher o formulário de edição. */
    suspend fun getProprietario(proprietarioId: Long): AppResult<ProprietarioReadModel, ApplicationError> =
        onDatabase { findProprietario(proprietarioId).toReadModel() }

    @Transaction
    @Query("SELECT * FROM property_owners WHERE id = :proprietarioId AND is_deleted = 0")
    protected abstract suspend fun findProprietario(proprietarioId: Long): ProprietarioCompound
}
