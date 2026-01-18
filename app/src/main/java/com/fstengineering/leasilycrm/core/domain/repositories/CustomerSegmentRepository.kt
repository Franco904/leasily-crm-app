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

    suspend fun getAll(): AppResult<ImmutableList<CustomerSegment>, DataError>
}
