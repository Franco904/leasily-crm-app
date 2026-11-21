package com.fstengineering.leasilycrm.imoveis.infra.dataSources

import androidx.room.Dao
import androidx.room.Query
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.infra.utils.asDatabaseFlowResult
import com.fstengineering.leasilycrm.core.infra.utils.escapeLike
import com.fstengineering.leasilycrm.core.infra.utils.onDatabase
import com.fstengineering.leasilycrm.imoveis.application.models.dtos.ImovelReadModel
import com.fstengineering.leasilycrm.imoveis.infra.mappers.toReadModel
import com.fstengineering.leasilycrm.imoveis.infra.projections.ImovelCardProjection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val SELECT_IMOVEL_CARD = """
    SELECT
        id,
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
    WHERE is_deleted = 0
"""

/** Leitura da listagem de Imóveis. */
@Dao
abstract class ImoveisReadDataSource {
    /** Observa os Imóveis não excluídos, em ordem alfabética de bairro. */
    fun watchImoveis(): Flow<AppResult<List<ImovelReadModel>, ApplicationError>> =
        observeImoveis()
            .map { projections -> projections.map { it.toReadModel() } }
            .asDatabaseFlowResult()

    /**
     * Lista os Imóveis não excluídos cujo bairro ou endereço contém [matching], sem diferenciar
     * maiúsculas de minúsculas, em ordem alfabética de bairro. Sem [matching], lista todos.
     */
    suspend fun getImoveis(
        matching: String?,
    ): AppResult<List<ImovelReadModel>, ApplicationError> = onDatabase {
        selectImoveis(matching?.escapeLike()).map { it.toReadModel() }
    }

    @Query("$SELECT_IMOVEL_CARD ORDER BY neighborhood")
    protected abstract fun observeImoveis(): Flow<List<ImovelCardProjection>>

    @Query(
        """
        $SELECT_IMOVEL_CARD
        AND (
            :matching IS NULL OR
            neighborhood LIKE '%' || :matching || '%' ESCAPE '\' COLLATE NOCASE OR
            street_address LIKE '%' || :matching || '%' ESCAPE '\' COLLATE NOCASE
        )
        ORDER BY neighborhood
        """
    )
    protected abstract suspend fun selectImoveis(matching: String?): List<ImovelCardProjection>
}
