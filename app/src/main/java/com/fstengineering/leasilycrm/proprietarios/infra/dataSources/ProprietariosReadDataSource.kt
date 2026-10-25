package com.fstengineering.leasilycrm.proprietarios.infra.dataSources

import androidx.room.Dao
import androidx.room.Query
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.proprietarios.infra.models.ProprietarioNameProjection
import com.fstengineering.leasilycrm.core.infra.utils.asDatabaseFlowResult
import com.fstengineering.leasilycrm.proprietarios.application.models.ProprietarioReadModel
import com.fstengineering.leasilycrm.proprietarios.infra.mappers.toProprietarioReadModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Leitura da listagem de Proprietários. */
@Dao
abstract class ProprietariosReadDataSource {
    /** Observa os Proprietários não excluídos, em ordem alfabética de nome. */
    fun watchProprietarios(): Flow<AppResult<List<ProprietarioReadModel>, ApplicationError>> =
        observeProprietarios()
            .map { projections -> projections.map { it.toProprietarioReadModel() } }
            .asDatabaseFlowResult()

    @Query(
        """
        SELECT id, name
        FROM property_owners
        WHERE is_deleted = 0
        ORDER BY name
        """
    )
    protected abstract fun observeProprietarios(): Flow<List<ProprietarioNameProjection>>
}
