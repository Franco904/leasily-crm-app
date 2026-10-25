package com.fstengineering.leasilycrm.propertyDashboard.infra.dataSources

import androidx.room.Dao
import androidx.room.Query
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.infra.utils.onDatabase
import com.fstengineering.leasilycrm.propertyDashboard.application.models.dtos.ProprietarioReadModel

/** Leitura do Proprietário no painel do Imóvel. */
@Dao
abstract class ProprietarioReadDataSource {
    /** Busca o Proprietário do Imóvel; falha quando ele não existe ou foi excluído. */
    suspend fun getProprietario(
        proprietarioId: Long,
    ): AppResult<ProprietarioReadModel, ApplicationError> = onDatabase {
        ProprietarioReadModel(id = proprietarioId, name = findProprietarioName(proprietarioId))
    }

    @Query(
        """
        SELECT name
        FROM property_owners
        WHERE id = :proprietarioId AND is_deleted = 0
        """
    )
    protected abstract suspend fun findProprietarioName(proprietarioId: Long): String
}
