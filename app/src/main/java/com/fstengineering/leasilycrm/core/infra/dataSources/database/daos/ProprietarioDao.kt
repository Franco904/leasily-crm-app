package com.fstengineering.leasilycrm.core.infra.dataSources.database.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ProprietarioOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.ProprietarioCompound
import kotlinx.coroutines.flow.Flow

@Dao
interface ProprietarioDao {
    @Upsert
    suspend fun insertOrUpdate(proprietario: ProprietarioOrm): Long

    @Query("UPDATE property_owners SET is_deleted = 1 WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("SELECT name FROM property_owners WHERE id = :id AND is_deleted = 0")
    suspend fun getNameById(id: Long): String

    @Transaction
    @Query("SELECT * FROM property_owners WHERE id = :id AND is_deleted = 0")
    suspend fun getByIdCompound(id: Long): ProprietarioCompound

    @Transaction
    @Query("SELECT * FROM property_owners WHERE id = :id AND is_deleted = 0")
    fun watchById(id: Long): Flow<ProprietarioCompound>

    @Query(
        """
        SELECT * FROM property_owners
        WHERE is_deleted = 0
        ORDER BY name
    """
    )
    fun watchAll(): Flow<List<ProprietarioOrm>>

    @Transaction
    @Query(
        """
        SELECT * FROM property_owners
        WHERE is_deleted = 0
        ORDER BY name
    """
    )
    fun watchAllCompound(): Flow<List<ProprietarioCompound>>
}
