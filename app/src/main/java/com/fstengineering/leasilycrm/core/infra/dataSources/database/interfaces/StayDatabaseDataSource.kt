package com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.StayEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.StayCheckStatusCompound
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.StayCompound
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.StayListCompound
import com.fstengineering.leasilycrm.core.infra.utils.InfraError
import kotlinx.coroutines.flow.Flow

interface StayDatabaseDataSource {
    suspend fun insertOrUpdate(stay: StayEntity): Long

    suspend fun delete(id: Long)

    suspend fun updateStatus(id: Long, status: Int)

    suspend fun getById(id: Long): AppResult<StayCompound, InfraError>

    suspend fun getAllByTenantId(tenantId: Long): AppResult<List<StayEntity>, InfraError>

    fun watchById(id: Long): AppResult<Flow<StayCompound?>, InfraError>

    fun watchAll(): AppResult<Flow<List<StayCompound>>, InfraError>

    fun watchAllWithDetails(): AppResult<Flow<List<StayListCompound>>, InfraError>

    fun watchAllByPropertyId(propertyId: Long): Flow<AppResult<List<StayEntity>, InfraError>>

    fun watchAllUpcomingCheckInsAndCheckOuts(): AppResult<Flow<List<StayCheckStatusCompound>>, InfraError>
}
