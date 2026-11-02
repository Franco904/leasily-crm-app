package com.fstengineering.leasilycrm.core.infra.dataSources.database

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.ImovelDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ImovelOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.ImovelCompound
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.ImovelDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.utils.asDatabaseFlowResult
import com.fstengineering.leasilycrm.core.infra.utils.onDatabase
import kotlinx.coroutines.flow.Flow

class ImovelDatabaseDataSourceImpl(
    private val dao: ImovelDao,
) : ImovelDatabaseDataSource {
    override suspend fun insertOrUpdate(imovel: ImovelOrm): Long =
        dao.insertOrUpdate(imovel)

    override suspend fun delete(id: Long) =
        dao.delete(id)

    override suspend fun getById(id: Long): AppResult<ImovelCompound, ApplicationError> {
        return onDatabase { dao.getById(id) }
    }

    override suspend fun getAll(matching: String?): AppResult<List<ImovelCompound>, ApplicationError> {
        return onDatabase { dao.getAll() }
    }

    override suspend fun getAllNeighborhoodNames(): AppResult<List<String>, ApplicationError> {
        return onDatabase { dao.getAllNeighborhoodNames() }
    }

    override suspend fun getAllSentInAssistance(
        assistanceId: Long,
    ): AppResult<List<ImovelCompound>, ApplicationError> {
        return onDatabase { dao.getAllSentInAssistance(assistanceId) }
    }

    override fun watchById(id: Long): Flow<AppResult<ImovelCompound, ApplicationError>> {
        return dao.watchById(id).asDatabaseFlowResult()
    }

    override fun watchAll(): AppResult<Flow<List<ImovelCompound>>, ApplicationError> {
        return onDatabase { dao.watchAll() }
    }
}
