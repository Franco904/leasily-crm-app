package com.fstengineering.leasilycrm.imovelRegistration.infra.dataSources

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.ImovelCompound
import com.fstengineering.leasilycrm.core.infra.utils.onDatabase
import com.fstengineering.leasilycrm.imovelRegistration.application.models.dtos.ImovelReadModel
import com.fstengineering.leasilycrm.imovelRegistration.application.models.dtos.ProprietarioReadModel
import com.fstengineering.leasilycrm.imovelRegistration.infra.mappers.toRawReadModel

/** Leitura do cadastro de Imóvel: o Imóvel a editar e os bairros já cadastrados. */
@Dao
abstract class ImovelRegistrationReadDataSource {
    /**
     * Busca o Imóvel não excluído com o seu Proprietário, Preços, arquivos e características;
     * falha quando o Imóvel ou o Proprietário não existe.
     */
    suspend fun getImovel(imovelId: Long): AppResult<ImovelReadModel, ApplicationError> =
        onDatabase {
            val imovel = requireNotNull(findImovel(imovelId))
            val proprietarioName =
                requireNotNull(findProprietarioName(imovel.imovel.proprietarioId))

            ImovelReadModel.create(
                imovelRaw = imovel.toRawReadModel(),
                proprietario = ProprietarioReadModel(
                    id = imovel.imovel.proprietarioId,
                    name = proprietarioName,
                ),
            )
        }

    /** Lista os bairros dos Imóveis não excluídos, sem repetição e em ordem alfabética. */
    suspend fun getNeighborhoodNames(): AppResult<List<String>, ApplicationError> =
        onDatabase { selectNeighborhoodNames() }

    @Transaction
    @Query("SELECT * FROM properties WHERE id = :imovelId AND is_deleted = 0")
    protected abstract suspend fun findImovel(imovelId: Long): ImovelCompound?

    @Query("SELECT name FROM property_owners WHERE id = :proprietarioId AND is_deleted = 0")
    protected abstract suspend fun findProprietarioName(proprietarioId: Long): String?

    @Query(
        """
        SELECT DISTINCT neighborhood
        FROM properties
        WHERE is_deleted = 0
        ORDER BY neighborhood
        """
    )
    protected abstract suspend fun selectNeighborhoodNames(): List<String>
}
