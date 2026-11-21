package com.fstengineering.leasilycrm.atendimentoRegistration.infra.dataSources

import androidx.room.Dao
import androidx.room.Query
import com.fstengineering.leasilycrm.atendimentoRegistration.application.models.AtendimentoSelectableImovelReadModel
import com.fstengineering.leasilycrm.atendimentoRegistration.infra.mappers.toReadModel
import com.fstengineering.leasilycrm.atendimentoRegistration.infra.projections.SelectableImovelProjection
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.infra.utils.escapeLike
import com.fstengineering.leasilycrm.core.infra.utils.onDatabase

/** Leitura dos Imóveis que o Corretor pode enviar ao Lead no Atendimento. */
@Dao
abstract class ImovelReadDataSource {
    /**
     * Lista os Imóveis não excluídos cujo bairro ou endereço contém [matching], sem diferenciar
     * maiúsculas de minúsculas, em ordem alfabética de bairro. Sem [matching], lista todos.
     */
    suspend fun getImoveis(
        matching: String? = null,
    ): AppResult<List<AtendimentoSelectableImovelReadModel>> = onDatabase {
        selectImoveis(matching?.escapeLike()).map { it.toReadModel() }
    }

    @Query(
        """
        SELECT
            id,
            property_type_id AS typeId,
            neighborhood,
            street_address AS streetAddress,
            number,
            property_sea_proximity_type_id AS seaProximityTypeId,
            distance_to_sea AS distanceToSea,
            bedrooms,
            (
                SELECT property_file_uri
                FROM property_files
                WHERE property_id = properties.id AND is_main_picture = 1
                LIMIT 1
            ) AS mainPictureUriString
        FROM properties
        WHERE is_deleted = 0
        AND (
            :matching IS NULL OR
            neighborhood LIKE '%' || :matching || '%' ESCAPE '\' COLLATE NOCASE OR
            street_address LIKE '%' || :matching || '%' ESCAPE '\' COLLATE NOCASE
        )
        ORDER BY neighborhood
        """
    )
    protected abstract suspend fun selectImoveis(matching: String?): List<SelectableImovelProjection>
}
