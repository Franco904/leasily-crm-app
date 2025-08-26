package com.fstengineering.leasilycrm.core.data.dataSources.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyOwnerEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.compounds.PropertyOwnerCompound
import kotlinx.coroutines.flow.Flow

@Dao
interface PropertyOwnerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(propertyOwner: PropertyOwnerEntity)

    @Query("UPDATE property_owners SET is_deleted = 1 WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("SELECT * FROM property_owners WHERE id = :id AND is_deleted = 0")
    suspend fun getById(id: Int): PropertyOwnerEntity

    @Transaction
    @Query("SELECT * FROM property_owners WHERE user_id = :userId AND is_deleted = 0")
    fun watchAll(userId: Int): Flow<List<PropertyOwnerCompound>>
}
