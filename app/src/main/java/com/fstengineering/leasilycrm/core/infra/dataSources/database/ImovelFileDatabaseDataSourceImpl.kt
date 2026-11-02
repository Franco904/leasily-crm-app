package com.fstengineering.leasilycrm.core.infra.dataSources.database

import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.ImovelFileDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ImovelFileOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.ImovelFileDatabaseDataSource

class ImovelFileDatabaseDataSourceImpl(
    private val dao: ImovelFileDao,
) : ImovelFileDatabaseDataSource {
    override suspend fun insertOrUpdate(
        imovelFile: ImovelFileOrm,
    ) =
        dao.insertOrUpdate(imovelFile)
}
