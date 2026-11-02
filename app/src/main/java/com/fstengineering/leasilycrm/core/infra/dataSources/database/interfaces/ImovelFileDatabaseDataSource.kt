package com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ImovelFileOrm

interface ImovelFileDatabaseDataSource {
    suspend fun insertOrUpdate(imovelFile: ImovelFileOrm)
}
