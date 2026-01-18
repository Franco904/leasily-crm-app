package com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.CustomerSegmentEntity
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import kotlinx.coroutines.flow.Flow

interface CustomerSegmentDatabaseDataSource {
    suspend fun insertOrUpdate(customerSegment: CustomerSegmentEntity)

    suspend fun delete(id: Long)

    suspend fun getAll(userId: Long): AppResult<List<CustomerSegmentEntity>, DataError>
}
