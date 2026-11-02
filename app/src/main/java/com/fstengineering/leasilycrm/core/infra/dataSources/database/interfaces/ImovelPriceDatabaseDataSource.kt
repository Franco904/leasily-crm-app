package com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ImovelPriceOrm

interface ImovelPriceDatabaseDataSource {
    suspend fun insertOrUpdate(imovelPrice: ImovelPriceOrm)
}
