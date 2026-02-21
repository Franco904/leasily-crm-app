package com.fstengineering.leasilycrm.core.application.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.entities.Assistance
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import kotlinx.collections.immutable.ImmutableList

interface AssistanceRepository {
    suspend fun insertOrUpdate(assistance: Assistance): AppResult<Unit, ApplicationError>

    suspend fun delete(id: Long): AppResult<Unit, ApplicationError>

    suspend fun getById(assistanceId: Long): AppResult<Assistance, ApplicationError>

    suspend fun getAllNeighborhoodNames(): AppResult<ImmutableList<String>, ApplicationError>
}
