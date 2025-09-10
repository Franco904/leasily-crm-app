package com.fstengineering.leasilycrm.core.domain.repositories

import com.fstengineering.leasilycrm.core.domain.models.CustomerSegment
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

interface CustomerSegmentRepository {
    suspend fun insertOrUpdate(
        customerSegment: CustomerSegment,
    ): AppResult<Unit, DataSourceError>

    suspend fun delete(id: Long): AppResult<Unit, DataSourceError>

    suspend fun getAllTenantType(): AppResult<ImmutableList<CustomerSegment>, DataSourceError>

    suspend fun getAllPropertyOwnerType(): AppResult<ImmutableList<CustomerSegment>, DataSourceError>

    fun watchAll(): Flow<AppResult<ImmutableList<CustomerSegment>, DataSourceError>>
}
