package com.fstengineering.leasilycrm.core.infra.dataSources.database

import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.ImovelPriceDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ImovelPriceOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.ImovelPriceDatabaseDataSource

class ImovelPriceDatabaseDataSourceImpl(
    private val dao: ImovelPriceDao,
) : ImovelPriceDatabaseDataSource {
    override suspend fun insertOrUpdate(
        imovelPrice: ImovelPriceOrm,
    ) =
        dao.insertOrUpdate(imovelPrice)
}
