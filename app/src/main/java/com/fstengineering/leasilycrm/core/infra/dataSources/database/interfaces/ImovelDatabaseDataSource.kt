package com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ApplicationError
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.ImovelCompound

interface ImovelDatabaseDataSource {
    suspend fun getById(id: Long): AppResult<ImovelCompound, ApplicationError>

    suspend fun getAll(matching: String? = null): AppResult<List<ImovelCompound>, ApplicationError>

    suspend fun getAllSentInAssistance(assistanceId: Long): AppResult<List<ImovelCompound>, ApplicationError>
}
