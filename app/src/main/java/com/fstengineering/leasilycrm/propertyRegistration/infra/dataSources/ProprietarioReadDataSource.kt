package com.fstengineering.leasilycrm.propertyRegistration.infra.dataSources

import androidx.room.Dao
import androidx.room.Query
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ProprietarioOrm
import com.fstengineering.leasilycrm.core.infra.utils.asDatabaseFlowResult
import com.fstengineering.leasilycrm.core.infra.utils.onDatabase
import com.fstengineering.leasilycrm.propertyRegistration.application.models.dtos.ProprietarioReadModel
import com.fstengineering.leasilycrm.propertyRegistration.infra.mappers.toProprietarioReadModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Leitura dos Proprietários no cadastro de Imóvel. */
@Dao
abstract class ProprietarioReadDataSource {
    /** Observa os Proprietários selecionáveis (não excluídos), em ordem alfabética de nome. */
    fun watchProprietarios(): Flow<AppResult<List<ProprietarioReadModel>, ApplicationError>> =
        observeProprietarios()
            .map { orms -> orms.map { it.toProprietarioReadModel() } }
            .asDatabaseFlowResult()

    /** Busca o Proprietário do Imóvel em edição; falha quando ele não existe ou foi excluído. */
    suspend fun getProprietario(
        proprietarioId: Long,
    ): AppResult<ProprietarioReadModel, ApplicationError> = onDatabase {
        ProprietarioReadModel(id = proprietarioId, name = findProprietarioName(proprietarioId))
    }

    @Query("SELECT * FROM property_owners WHERE is_deleted = 0 ORDER BY name")
    protected abstract fun observeProprietarios(): Flow<List<ProprietarioOrm>>

    @Query("SELECT name FROM property_owners WHERE id = :proprietarioId AND is_deleted = 0")
    protected abstract suspend fun findProprietarioName(proprietarioId: Long): String
}
