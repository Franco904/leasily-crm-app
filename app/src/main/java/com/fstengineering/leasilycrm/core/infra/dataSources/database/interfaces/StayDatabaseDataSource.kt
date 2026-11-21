package com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.StayEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.OccupiedPeriodListProjection
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.OccupiedPeriodNotificationProjection
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.StayCheckInCheckOutCountsProjection
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.StayCompound
import kotlinx.coroutines.flow.Flow

interface StayDatabaseDataSource {
    suspend fun insertOrUpdate(stay: StayEntity): Long

    suspend fun delete(id: Long)

    suspend fun updateStatus(id: Long, status: Int)

    suspend fun getById(id: Long): AppResult<StayCompound>

    suspend fun getAllByTenantId(tenantId: Long): AppResult<List<StayEntity>>

    suspend fun getCurrentStayIdByTenantId(tenantId: Long): AppResult<Long?>

    fun watchById(id: Long): AppResult<Flow<StayCompound?>>

    fun watchAll(): AppResult<Flow<List<StayCompound>>>

    fun watchAllWithDetails(): AppResult<Flow<List<OccupiedPeriodListProjection>>>

    fun watchAllByImovelId(imovelId: Long): Flow<AppResult<List<StayEntity>>>

    fun getAllTodayCheckInsAndCheckOutsToNotify(): AppResult<List<OccupiedPeriodNotificationProjection>>

    fun watchAllPastCheckInCheckOutNotDoneCounts(): Flow<AppResult<StayCheckInCheckOutCountsProjection>>
}
