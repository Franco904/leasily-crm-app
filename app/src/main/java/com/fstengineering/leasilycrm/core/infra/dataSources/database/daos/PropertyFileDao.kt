package com.fstengineering.leasilycrm.core.infra.dataSources.database.daos

import androidx.room.Dao
import androidx.room.Upsert
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.PropertyFileEntity

@Dao
interface PropertyFileDao {
    @Upsert
    suspend fun insertOrUpdate(propertyFile: PropertyFileEntity)
}
