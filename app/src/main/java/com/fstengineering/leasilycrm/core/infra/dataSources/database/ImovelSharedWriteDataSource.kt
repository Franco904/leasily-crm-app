package com.fstengineering.leasilycrm.core.infra.dataSources.database

import androidx.room.Dao
import androidx.room.Query
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.infra.utils.onDatabase

/** Escrita de Imóvel compartilhada pela listagem, pelos detalhes e pelo dashboard. */
@Dao
abstract class ImovelSharedWriteDataSource {
    /** Exclui o Imóvel, que deixa de aparecer na listagem e nos detalhes. */
    suspend fun delete(imovelId: Long): AppResult<Unit> =
        onDatabase { markAsDeleted(imovelId) }

    @Query(
        """
        UPDATE properties
        SET is_deleted = 1
        WHERE id = :imovelId
        """
    )
    protected abstract suspend fun markAsDeleted(imovelId: Long)
}
