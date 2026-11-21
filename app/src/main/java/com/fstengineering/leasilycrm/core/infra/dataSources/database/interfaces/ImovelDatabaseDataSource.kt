package com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.ImovelCompound

interface ImovelDatabaseDataSource {
    suspend fun getById(id: Long): AppResult<ImovelCompound>

    suspend fun getAll(matching: String? = null): AppResult<List<ImovelCompound>>
}
