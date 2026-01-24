package com.fstengineering.leasilycrm.core.data.dataSources.database.daos

import androidx.room.Dao
import androidx.room.Upsert
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.PropertyCharacteristicPropertyCrossRefEntity

@Dao
interface PropertyCharacteristicPropertyCrossRefDao {
    @Upsert
    suspend fun insertOrUpdate(crossRef: PropertyCharacteristicPropertyCrossRefEntity)
}
