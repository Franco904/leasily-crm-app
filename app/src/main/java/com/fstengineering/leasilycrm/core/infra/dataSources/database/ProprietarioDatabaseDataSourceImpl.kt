package com.fstengineering.leasilycrm.core.infra.dataSources.database

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.ProprietarioDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ProprietarioOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.ProprietarioCompound
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.ProprietarioDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.utils.onDatabase
import kotlinx.coroutines.flow.Flow

class ProprietarioDatabaseDataSourceImpl(
    private val dao: ProprietarioDao,
) : ProprietarioDatabaseDataSource {
    override suspend fun insertOrUpdate(proprietario: ProprietarioOrm): Long =
        dao.insertOrUpdate(proprietario)

    override suspend fun delete(id: Long) =
        dao.delete(id)

    override suspend fun getNameById(id: Long): AppResult<String, ApplicationError> {
        return onDatabase { dao.getNameById(id) }
    }

    override suspend fun getByIdCompound(id: Long): AppResult<ProprietarioCompound, ApplicationError> {
        return onDatabase { dao.getByIdCompound(id) }
    }

    override fun watchById(id: Long): AppResult<Flow<ProprietarioCompound>, ApplicationError> {
        return onDatabase { dao.watchById(id) }
    }

    override fun watchAll(): AppResult<Flow<List<ProprietarioOrm>>, ApplicationError> {
        return onDatabase { dao.watchAll() }
    }

    override fun watchAllCompound(): AppResult<Flow<List<ProprietarioCompound>>, ApplicationError> {
        return onDatabase { dao.watchAllCompound() }
    }
}
