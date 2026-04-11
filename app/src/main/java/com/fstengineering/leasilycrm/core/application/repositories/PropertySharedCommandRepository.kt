package com.fstengineering.leasilycrm.core.application.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.valueObjects.CalendarMonthYear
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

interface PropertySharedCommandRepository {
    suspend fun delete(id: Long): AppResult<Unit, ApplicationError>

    suspend fun getAllMatching(matching: String?): AppResult<ImmutableList<Property>, ApplicationError>

    fun watchAll(): Flow<AppResult<ImmutableList<Property>, ApplicationError>>
}
