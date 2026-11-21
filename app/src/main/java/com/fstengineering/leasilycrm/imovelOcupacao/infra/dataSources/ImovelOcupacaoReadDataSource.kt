package com.fstengineering.leasilycrm.imovelOcupacao.infra.dataSources

import androidx.room.Dao
import androidx.room.Query
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.infra.utils.asDatabaseFlowResult
import com.fstengineering.leasilycrm.imovelOcupacao.application.models.dtos.ImovelPeriodoOcupadoRawReadModel
import com.fstengineering.leasilycrm.imovelOcupacao.infra.mappers.toReadModel
import com.fstengineering.leasilycrm.imovelOcupacao.infra.projections.PeriodoOcupadoProjection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Leitura dos Períodos Ocupados de um Imóvel para o calendário de ocupação. */
@Dao
abstract class ImovelOcupacaoReadDataSource {
    /** Observa os Períodos Ocupados não excluídos do Imóvel, em ordem de check-in. */
    fun watchPeriodosOcupados(
        imovelId: Long,
    ): Flow<AppResult<List<ImovelPeriodoOcupadoRawReadModel>, ApplicationError>> =
        observePeriodosOcupados(imovelId)
            .map { projections -> projections.map { it.toReadModel() } }
            .asDatabaseFlowResult()

    @Query(
        """
        SELECT
            id,
            tenant_id AS tenantId,
            check_in_date_time AS checkInDateTime,
            check_out_date_time AS checkOutDateTime
        FROM stays
        WHERE property_id = :imovelId AND is_deleted = 0
        ORDER BY check_in_date_time
        """
    )
    protected abstract fun observePeriodosOcupados(imovelId: Long): Flow<List<PeriodoOcupadoProjection>>
}
