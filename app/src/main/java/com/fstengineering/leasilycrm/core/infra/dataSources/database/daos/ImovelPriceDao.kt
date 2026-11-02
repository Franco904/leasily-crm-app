package com.fstengineering.leasilycrm.core.infra.dataSources.database.daos

import androidx.room.Dao
import androidx.room.Upsert
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ImovelPriceOrm

@Dao
interface ImovelPriceDao {
    @Upsert
    suspend fun insertOrUpdate(imovelPrice: ImovelPriceOrm)
}
