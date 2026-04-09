package com.fstengineering.leasilycrm.core.infra.dataSources.database.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.PropertyCompound
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
        WHERE is_deleted = 0
        AND (
            :matching IS NULL OR
            neighborhood   LIKE '%' || :matching || '%' ESCAPE '\' COLLATE NOCASE OR
            street_address LIKE '%' || :matching || '%' ESCAPE '\' COLLATE NOCASE
        )
        ORDER BY neighborhood
    """
    )
    suspend fun getAll(matching: String? = null): List<PropertyCompound>

    @Query(
        """
        SELECT DISTINCT(neighborhood) FROM properties
        WHERE is_deleted = 0
        ORDER BY neighborhood
    """
    )
    suspend fun getAllNeighborhoodNames(): List<String>

    @Query(
        """
        SELECT * FROM properties
        INNER JOIN assistance_sent_properties asp ON properties.id = asp.property_id
        WHERE asp.assistance_id = :assistanceId
        AND is_deleted = 0
    """
    )
    suspend fun getAllSentInAssistance(assistanceId: Long): List<PropertyCompound>

    @Transaction
    @Query("SELECT * FROM properties WHERE id = :id AND is_deleted = 0")
    fun watchById(id: Long): Flow<PropertyCompound>

    @Transaction
    @Query(
        """
        SELECT * FROM properties
        WHERE is_deleted = 0
        ORDER BY neighborhood
    """
    )
    fun watchAll(): Flow<List<PropertyCompound>>
}
