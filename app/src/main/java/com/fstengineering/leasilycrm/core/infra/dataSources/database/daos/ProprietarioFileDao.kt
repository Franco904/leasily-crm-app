package com.fstengineering.leasilycrm.core.infra.dataSources.database.daos

import androidx.room.Dao
import androidx.room.Upsert
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ProprietarioFileOrm

@Dao
interface ProprietarioFileDao {
    @Upsert
    suspend fun insertOrUpdate(proprietarioFile: ProprietarioFileOrm)
}
