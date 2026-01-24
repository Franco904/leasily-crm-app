package com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.StayEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.compounds.StayCheckStatusCompound
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.compounds.StayCompound
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import kotlinx.coroutines.flow.Flow

interface StayDatabaseDataSource {
    suspend fun insertOrUpdate(stay: StayEntity): Long

    suspend fun delete(id: Long)

    suspend fun getById(id: Long): AppResult<StayCompound, DataError>

    fun watchById(id: Long): AppResult<Flow<StayCompound?>, DataError>

    fun watchAll(userId: Long): AppResult<Flow<List<StayCompound>>, DataError>

    fun watchAllUpcomingCheckInsAndCheckOuts(
        userId: Long,
    ): AppResult<Flow<List<StayCheckStatusCompound>>, DataError>
}
