package com.fstengineering.leasilycrm.core.data.dataSources.database.interfaces

import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.CustomerSegmentEntity
import com.fstengineering.leasilycrm.core.data.dataSources.database.entities.compounds.CustomerSegmentCompound
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import kotlinx.coroutines.flow.Flow

interface CustomerSegmentDatabaseDataSource {
    suspend fun insertOrUpdate(customerSegment: CustomerSegmentEntity): AppResult<Unit, DataError>

    suspend fun delete(id: Long): AppResult<Unit, DataError>

    suspend fun getById(id: Long): AppResult<CustomerSegmentEntity, DataError>

    suspend fun getAllTenantType(userId: Long): AppResult<List<CustomerSegmentCompound>, DataError>

    suspend fun getAllPropertyOwnerType(userId: Long): AppResult<List<CustomerSegmentCompound>, DataError>

    fun watchAll(userId: Long): AppResult<Flow<List<CustomerSegmentCompound>>, DataError>

    suspend fun countCustomerSegmentsWithName(
        userId: Long,
        customerSegmentName: String,
        customerSegmentTypeId: Long,
        currentStoredCustomerSegmentName: String?,
    ): AppResult<Int, DataError>
}
