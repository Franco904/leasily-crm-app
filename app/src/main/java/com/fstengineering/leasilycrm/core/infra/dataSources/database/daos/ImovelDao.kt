package com.fstengineering.leasilycrm.core.infra.dataSources.database.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.ImovelCompound

@Dao
interface ImovelDao {
    @Transaction
    @Query("SELECT * FROM properties WHERE id = :id AND is_deleted = 0")
    suspend fun getById(id: Long): ImovelCompound

    @Transaction
    @Query(
        """
        SELECT * FROM properties
        WHERE is_deleted = 0
        AND (
            :matching IS NULL OR
            neighborhood   LIKE '%' || :matching || '%' ESCAPE '\' COLLATE NOCASE OR
            street_address LIKE '%' || :matching || '%' ESCAPE '\' COLLATE NOCASE
        )
        ORDER BY neighborhood
    """
    )
    suspend fun getAll(matching: String? = null): List<ImovelCompound>
}
