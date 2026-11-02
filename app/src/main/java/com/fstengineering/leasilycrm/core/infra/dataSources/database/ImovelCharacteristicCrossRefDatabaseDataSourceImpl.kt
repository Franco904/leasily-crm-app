package com.fstengineering.leasilycrm.core.infra.dataSources.database

import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.ImovelCharacteristicImovelCrossRefDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ImovelCharacteristicCrossRefOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.ImovelCharacteristicCrossRefDatabaseDataSource

class ImovelCharacteristicCrossRefDatabaseDataSourceImpl(
    private val dao: ImovelCharacteristicImovelCrossRefDao,
) : ImovelCharacteristicCrossRefDatabaseDataSource {
    override suspend fun insertOrUpdate(
        crossRef: ImovelCharacteristicCrossRefOrm,
    ) =
        dao.insertOrUpdate(crossRef)
}
