package com.fstengineering.leasilycrm.core.infra.dataSources.database.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.AssistanceCharacteristicEntity

@Dao
interface AssistanceCharacteristicDao {
    @Upsert
    suspend fun insertOrUpdate(assistanceCharacteristic: AssistanceCharacteristicEntity)

    @Query("SELECT * FROM assistance_characteristics")
    suspend fun getAll(): List<AssistanceCharacteristicEntity>
}
