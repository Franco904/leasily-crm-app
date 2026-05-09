package com.fstengineering.leasilycrm.core.infra.dataSources.database

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime
import com.fstengineering.leasilycrm.core.infra.dataSources.database.daos.StayDao
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.StayEntity
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.OccupiedPeriodListProjection
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.OccupiedPeriodNotificationProjection
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.StayCheckInCheckOutCountsProjection
import com.fstengineering.leasilycrm.core.infra.dataSources.database.entities.compounds.StayCompound
import com.fstengineering.leasilycrm.core.infra.dataSources.database.interfaces.StayDatabaseDataSource
import com.fstengineering.leasilycrm.core.infra.utils.InfraError
import kotlinx.coroutines.flow.Flow

class StayDatabaseDataSourceImpl(
    private val dao: StayDao,
    private val now: AppDateTime,
) : StayDatabaseDataSource {
    override suspend fun insertOrUpdate(stay: StayEntity): Long =
        dao.insertOrUpdate(stay)

    override suspend fun delete(id: Long) =
        dao.delete(id)

    override suspend fun updateStatus(id: Long, status: Int) =
        dao.updateStatus(id, status)

    override suspend fun getById(id: Long): AppResult<StayCompound, InfraError> {
        return onDatabaseSafe { dao.getById(id) }
    }

    override suspend fun getAllByTenantId(tenantId: Long): AppResult<List<StayEntity>, InfraError> {
        return onDatabaseSafe { dao.getAllByTenantId(tenantId) }
    }

    override suspend fun getCurrentStayIdByTenantId(tenantId: Long): AppResult<Long?, InfraError> {
        return onDatabaseSafe { dao.getCurrentStayIdByTenantId(tenantId) }
    }

    override fun watchById(id: Long): AppResult<Flow<StayCompound?>, InfraError> {
        return onDatabaseSafe { dao.watchById(id) }
    }

    override fun watchAll(): AppResult<Flow<List<StayCompound>>, InfraError> {
        return onDatabaseSafe { dao.watchAll() }
    }

    override fun watchAllWithDetails(): AppResult<Flow<List<OccupiedPeriodListProjection>>, InfraError> {
        return onDatabaseSafe { dao.watchAllWithDetails() }
    }

    override fun watchAllByPropertyId(propertyId: Long): Flow<AppResult<List<StayEntity>, InfraError>> {
        return dao.watchAllByPropertyId(propertyId).asDatabaseFlowResult()
    }

    override fun getAllTodayCheckInsAndCheckOutsToNotify(): AppResult<List<OccupiedPeriodNotificationProjection>, InfraError> {
        return onDatabaseSafe { dao.getAllTodayCheckInsAndCheckOutsToNotify(now.toEpochMillisUTC()) }
    }

    override fun watchAllPastCheckInCheckOutNotDoneCounts(): Flow<AppResult<StayCheckInCheckOutCountsProjection, InfraError>> {
        val nowAdjusted = now.withHourMinute(23, 59)
        return dao.watchAllPastCheckInCheckOutNotDoneCounts(nowAdjusted).asDatabaseFlowResult()
    }
}
