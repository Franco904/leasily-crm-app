package com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.ImovelOrm
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.ImovelCompound
import kotlinx.coroutines.flow.Flow

interface ImovelDatabaseDataSource {
    suspend fun insertOrUpdate(imovel: ImovelOrm): Long

    suspend fun delete(id: Long)

    suspend fun getById(id: Long): AppResult<ImovelCompound, ApplicationError>

    suspend fun getAll(matching: String? = null): AppResult<List<ImovelCompound>, ApplicationError>

    suspend fun getAllNeighborhoodNames(): AppResult<List<String>, ApplicationError>

    suspend fun getAllSentInAssistance(assistanceId: Long): AppResult<List<ImovelCompound>, ApplicationError>

    fun watchById(id: Long): Flow<AppResult<ImovelCompound, ApplicationError>>

    fun watchAll(): AppResult<Flow<List<ImovelCompound>>, ApplicationError>
}
