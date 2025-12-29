package com.fstengineering.leasilycrm.core.domain.repositories

import com.fstengineering.leasilycrm.core.domain.models.CustomerSegment
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

interface CustomerSegmentRepository {
    suspend fun insertOrUpdate(
        customerSegment: CustomerSegment,
    ): AppResult<Unit, DataError>

    suspend fun delete(id: Long): AppResult<Unit, DataError>

    suspend fun getAllTenantType(): AppResult<ImmutableList<CustomerSegment>, DataError>

    suspend fun getAllPropertyOwnerType(): AppResult<ImmutableList<CustomerSegment>, DataError>

    fun watchAll(): Flow<AppResult<ImmutableList<CustomerSegment>, DataError>>

    suspend fun isThereCustomerSegmentWithName(
        customerSegmentName: String,
        customerSegmentTypeId: Long,
        currentStoredCustomerSegmentName: String?,
    ): AppResult<Boolean, DataError>
}
