package com.fstengineering.leasilycrm.core.domain.repositories

import com.fstengineering.leasilycrm.core.domain.models.Assistance
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

interface AssistanceRepository {
    suspend fun insertOrUpdate(assistance: Assistance): AppResult<Long, DataSourceError>

    suspend fun delete(id: Long): AppResult<Unit, DataSourceError>

    suspend fun getById(assistanceId: Long): AppResult<Assistance, DataSourceError>

    suspend fun getAllNeighborhoodNames(): AppResult<ImmutableList<String>, DataSourceError>

    fun watchById(assistanceId: Long): Flow<AppResult<Assistance?, DataSourceError>>

    fun watchAllByTenant(tenantId: Long): Flow<AppResult<ImmutableList<Assistance>, DataSourceError>>
}
