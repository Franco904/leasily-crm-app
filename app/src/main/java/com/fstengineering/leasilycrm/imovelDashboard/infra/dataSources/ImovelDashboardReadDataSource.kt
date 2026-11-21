package com.fstengineering.leasilycrm.imovelDashboard.infra.dataSources

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.infra.utils.asDatabaseFlowResult
import com.fstengineering.leasilycrm.imovelDashboard.application.models.dtos.ImovelPeriodoOcupadoRawReadModel
import com.fstengineering.leasilycrm.imovelDashboard.application.models.dtos.ImovelSummaryReadModel
import com.fstengineering.leasilycrm.imovelDashboard.infra.mappers.toReadModel
import com.fstengineering.leasilycrm.imovelDashboard.infra.projections.ImovelDashboardProjection
import com.fstengineering.leasilycrm.imovelDashboard.infra.projections.PeriodoOcupadoProjection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

/** Leitura do Imóvel e dos seus Períodos Ocupados para o painel do Imóvel. */
@Dao
abstract class ImovelDashboardReadDataSource {
    /** Observa o Imóvel não excluído com a foto principal e o Proprietário; falha quando o
     * Proprietário foi excluído. */
    fun watchImovel(imovelId: Long): Flow<AppResult<ImovelSummaryReadModel, ApplicationError>> =
        observeImovel(imovelId)
            .filterNotNull()
            .map { it.toReadModel() }
            .asDatabaseFlowResult()

    /** Observa os Períodos Ocupados não excluídos do Imóvel, em ordem de check-in. */
    fun watchPeriodosOcupados(
        imovelId: Long,
    ): Flow<AppResult<List<ImovelPeriodoOcupadoRawReadModel>, ApplicationError>> =
        observePeriodosOcupados(imovelId)
            .map { projections -> projections.map { it.toReadModel() } }
            .asDatabaseFlowResult()

    @Transaction
    @Query(
        """
        SELECT
            id,
            property_owner_id AS proprietarioId,
            property_type_id AS typeId,
            neighborhood,
            street_address AS streetAddress,
            number,
            (
                SELECT property_file_uri
                FROM property_files
                WHERE property_id = properties.id AND is_main_picture = 1
                LIMIT 1
            ) AS mainPictureUriString
        FROM properties
        WHERE id = :imovelId AND is_deleted = 0
        """
    )
    protected abstract fun observeImovel(imovelId: Long): Flow<ImovelDashboardProjection?>

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
