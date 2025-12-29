package com.fstengineering.leasilycrm.core.domain.repositories

import com.fstengineering.leasilycrm.core.domain.models.Assistance
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

interface AssistanceRepository {
    suspend fun insertOrUpdate(assistance: Assistance): AppResult<Long, DataError>

    suspend fun delete(id: Long): AppResult<Unit, DataError>

    suspend fun getById(assistanceId: Long): AppResult<Assistance, DataError>

    suspend fun getAllNeighborhoodNames(): AppResult<ImmutableList<String>, DataError>

    fun watchById(assistanceId: Long): Flow<AppResult<Assistance?, DataError>>

    fun watchAllByTenant(tenantId: Long): Flow<AppResult<ImmutableList<Assistance>, DataError>>
}
