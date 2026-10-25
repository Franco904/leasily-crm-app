package com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ProprietarioOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.ProprietarioCompound
import kotlinx.coroutines.flow.Flow

interface ProprietarioDatabaseDataSource {
    suspend fun insertOrUpdate(proprietario: ProprietarioOrm): Long

    suspend fun delete(id: Long)

    suspend fun getNameById(id: Long): AppResult<String, ApplicationError>

    suspend fun getByIdCompound(id: Long): AppResult<ProprietarioCompound, ApplicationError>

    fun watchById(id: Long): AppResult<Flow<ProprietarioCompound>, ApplicationError>

    fun watchAll(): AppResult<Flow<List<ProprietarioOrm>>, ApplicationError>

    fun watchAllCompound(): AppResult<Flow<List<ProprietarioCompound>>, ApplicationError>
}
