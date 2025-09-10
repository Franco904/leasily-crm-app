package com.fstengineering.leasilycrm.core.data.dataSources.database.daos

import androidx.room.Dao
import androidx.room.Upsert
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.AssistanceCharacteristicEntity

@Dao
interface AssistanceCharacteristicDao {
    @Upsert
    suspend fun insertOrUpdate(assistanceCharacteristic: AssistanceCharacteristicEntity): Long
}
