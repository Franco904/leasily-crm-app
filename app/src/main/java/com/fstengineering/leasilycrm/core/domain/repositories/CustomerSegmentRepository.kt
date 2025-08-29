package com.fstengineering.leasilycrm.core.domain.repositories

import com.fstengineering.leasilycrm.core.domain.models.CustomerSegment
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

interface CustomerSegmentRepository {
    suspend fun insert(
        customerSegment: CustomerSegment,
    ): AppResult<Unit, DataSourceError>

    suspend fun update(
        customerSegment: CustomerSegment,
    ): AppResult<Unit, DataSourceError>

    suspend fun delete(id: Int): AppResult<Unit, DataSourceError>

    fun watchAll(): Flow<AppResult<ImmutableList<CustomerSegment>, DataSourceError>>
}
