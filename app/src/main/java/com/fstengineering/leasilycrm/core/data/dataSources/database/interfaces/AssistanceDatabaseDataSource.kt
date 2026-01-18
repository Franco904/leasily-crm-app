package com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.AssistanceEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.compounds.AssistanceCompound
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError

interface AssistanceDatabaseDataSource {
    suspend fun insertOrUpdate(assistance: AssistanceEntity): AppResult<Long, DataError>

    suspend fun delete(id: Long): AppResult<Unit, DataError>

    suspend fun getById(id: Long): AppResult<AssistanceCompound, DataError>

    suspend fun getAllNeighborhoodNames(userId: Long): AppResult<List<String>, DataError>
}
