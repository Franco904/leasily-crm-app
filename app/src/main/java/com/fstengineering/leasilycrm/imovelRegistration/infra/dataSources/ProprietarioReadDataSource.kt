package com.fstengineering.leasilycrm.imovelRegistration.infra.dataSources

import androidx.room.Dao
import androidx.room.Query
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.infra.utils.asDatabaseFlowResult
import com.fstengineering.leasilycrm.imovelRegistration.application.models.dtos.ProprietarioReadModel
import com.fstengineering.leasilycrm.imovelRegistration.infra.mappers.toReadModel
import com.fstengineering.leasilycrm.imovelRegistration.infra.projections.ProprietarioNameProjection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Leitura dos Proprietários no cadastro de Imóvel. */
@Dao
abstract class ProprietarioReadDataSource {
    /** Observa os Proprietários selecionáveis (não excluídos), em ordem alfabética de nome. */
    fun watchProprietarios(): Flow<AppResult<List<ProprietarioReadModel>, ApplicationError>> =
        observeProprietarios()
            .map { projections -> projections.map { it.toReadModel() } }
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
