package com.fstengineering.leasilycrm.core.data.dataSources.database.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.compounds.PropertyCompound
import kotlinx.coroutines.flow.Flow

@Dao
interface PropertyDao {
    @Upsert
    suspend fun insertOrUpdate(property: PropertyEntity): Long

    @Query("UPDATE properties SET is_deleted = 1 WHERE id = :id")
    suspend fun delete(id: Long)

    @Transaction
    @Query("SELECT * FROM properties WHERE id = :id AND is_deleted = 0")
    suspend fun getById(id: Long): PropertyCompound

    @Transaction
    @Query(
        """
        SELECT * FROM properties
        WHERE user_id = :userId
        AND is_deleted = 0
        ORDER BY update_date_time DESC
    """
    )
    suspend fun getAll(userId: Long): List<PropertyCompound>

    @Transaction
    @Query(
        """
        SELECT * FROM properties
        WHERE user_id = :userId
        AND is_deleted = 0
        AND (
            :matching IS NULL OR
            neighborhood   LIKE '%' || :matching || '%' ESCAPE '\' COLLATE NOCASE OR
            street_address LIKE '%' || :matching || '%' ESCAPE '\' COLLATE NOCASE
        )
        ORDER BY update_date_time DESC
    """
    )
    suspend fun getAllMatching(userId: Long, matching: String?): List<PropertyCompound>

    @Query(
        """
        SELECT DISTINCT(neighborhood) FROM properties
        WHERE user_id = :userId
        AND is_deleted = 0
        ORDER BY neighborhood
    """
    )
    suspend fun getAllNeighborHoodNames(userId: Long): List<String>

    @Transaction
    @Query("SELECT * FROM properties WHERE id = :id AND is_deleted = 0")
    fun watchById(id: Long): Flow<PropertyCompound?>

    @Transaction
    @Query(
        """
        SELECT * FROM properties
        WHERE user_id = :userId
        AND is_deleted = 0
        ORDER BY update_date_time DESC
    """
    )
    fun watchAll(userId: Long): Flow<List<PropertyCompound>>
}
