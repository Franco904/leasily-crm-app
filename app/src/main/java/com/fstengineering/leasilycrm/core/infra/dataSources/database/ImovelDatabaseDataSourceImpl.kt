package com.fstengineering.leasilycrm.core.infra.dataSources.database

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.ImovelDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.ImovelCompound
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.ImovelDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.utils.onDatabase

class ImovelDatabaseDataSourceImpl(
    private val dao: ImovelDao,
) : ImovelDatabaseDataSource {
    override suspend fun getById(id: Long): AppResult<ImovelCompound> {
        return onDatabase { dao.getById(id) }
    }

    override suspend fun getAll(matching: String?): AppResult<List<ImovelCompound>> {
        return onDatabase { dao.getAll() }
    }
}
