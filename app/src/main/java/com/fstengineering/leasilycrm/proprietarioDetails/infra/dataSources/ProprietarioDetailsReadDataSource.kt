package com.fstengineering.leasilycrm.proprietarioDetails.infra.dataSources

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.ProprietarioCompound
import com.fstengineering.leasilycrm.core.infra.utils.asDatabaseFlowResult
import com.fstengineering.leasilycrm.proprietarioDetails.application.models.ProprietarioReadModel
import com.fstengineering.leasilycrm.proprietarioDetails.infra.mappers.toReadModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Leitura dos detalhes de um Proprietário. */
@Dao
abstract class ProprietarioDetailsReadDataSource {
    /** Observa o Proprietário com seus arquivos; emite `null` quando ele não existe ou foi excluído. */
    fun watchProprietario(
        proprietarioId: Long,
    ): Flow<AppResult<ProprietarioReadModel?, ApplicationError>> =
        observeProprietario(proprietarioId)
            .map { it?.toReadModel() }
            .asDatabaseFlowResult()

    @Transaction
    @Query("SELECT * FROM property_owners WHERE id = :proprietarioId AND is_deleted = 0")
    protected abstract fun observeProprietario(proprietarioId: Long): Flow<ProprietarioCompound?>
}
