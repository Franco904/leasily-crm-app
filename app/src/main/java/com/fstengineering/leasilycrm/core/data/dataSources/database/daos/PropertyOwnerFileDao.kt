package com.fstengineering.leasilycrm.core.data.dataSources.database.daos

import androidx.room.Dao
import androidx.room.Upsert
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyOwnerFileEntity

@Dao
interface PropertyOwnerFileDao {
    @Upsert
    suspend fun insertOrUpdate(propertyOwnerFile: PropertyOwnerFileEntity)
}
