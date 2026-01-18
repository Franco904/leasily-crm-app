package com.fstengineering.leasilycrm.core.domain.repositories

import com.fstengineering.leasilycrm.core.domain.models.Assistance
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import kotlinx.collections.immutable.ImmutableList

interface AssistanceRepository {
    suspend fun insertOrUpdate(assistance: Assistance): AppResult<Long, DataError>

    suspend fun delete(id: Long): AppResult<Unit, DataError>

    suspend fun getById(assistanceId: Long): AppResult<Assistance, DataError>

    suspend fun getAllNeighborhoodNames(): AppResult<ImmutableList<String>, DataError>
}
