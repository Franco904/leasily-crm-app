package com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ImovelCharacteristicCrossRefOrm

interface ImovelCharacteristicCrossRefDatabaseDataSource {
    suspend fun insertOrUpdate(
        crossRef: ImovelCharacteristicCrossRefOrm,
    )
}
