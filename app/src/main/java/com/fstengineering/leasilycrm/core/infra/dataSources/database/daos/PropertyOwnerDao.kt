package com.fstengineering.leasilycrm.core.infra.dataSources.database.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyOwnerEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.PropertyOwnerCompound
import kotlinx.coroutines.flow.Flow

@Dao
interface PropertyOwnerDao {
    @Upsert
    suspend fun insertOrUpdate(propertyOwner: PropertyOwnerEntity): Long

    @Query("UPDATE property_owners SET is_deleted = 1 WHERE id = :id")
    suspend fun delete(id: Long)

    @Transaction
    @Query("SELECT * FROM property_owners WHERE id = :id AND is_deleted = 0")
    suspend fun getById(id: Long): PropertyOwnerCompound

    @Transaction
    @Query("SELECT * FROM property_owners WHERE id = :id AND is_deleted = 0")
    fun watchById(id: Long): Flow<PropertyOwnerCompound>

    @Transaction
    @Query(
        """
        SELECT * FROM property_owners
        WHERE is_deleted = 0
        ORDER BY name
    """
    )
    fun watchAll(): Flow<List<PropertyOwnerCompound>>
}
