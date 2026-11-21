package com.fstengineering.leasilycrm.imovelDetails.infra.dataSources

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.ImovelCompound
import com.fstengineering.leasilycrm.core.infra.utils.asDatabaseFlowResult
import com.fstengineering.leasilycrm.imovelDetails.application.models.dtos.ImovelReadModel
import com.fstengineering.leasilycrm.imovelDetails.infra.mappers.toReadModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

/** Leitura dos detalhes de um Imóvel. */
@Dao
abstract class ImovelDetailsReadDataSource {
    /** Observa o Imóvel não excluído com características, Preços e arquivos. */
    fun watchImovel(imovelId: Long): Flow<AppResult<ImovelReadModel, ApplicationError>> =
        observeImovel(imovelId)
            .filterNotNull()
            .map { it.toReadModel() }
            .asDatabaseFlowResult()

    @Transaction
    @Query("SELECT * FROM properties WHERE id = :imovelId AND is_deleted = 0")
    protected abstract fun observeImovel(imovelId: Long): Flow<ImovelCompound?>
}
