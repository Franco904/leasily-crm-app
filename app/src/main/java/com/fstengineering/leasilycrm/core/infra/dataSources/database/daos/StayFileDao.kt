package com.fstengineering.leasilycrm.core.infra.dataSources.database.daos

import androidx.room.Dao
import androidx.room.Upsert
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.StayFileEntity

@Dao
interface StayFileDao {
    @Upsert
    suspend fun insertOrUpdate(stayFile: StayFileEntity)
}
