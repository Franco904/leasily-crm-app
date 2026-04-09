package com.fstengineering.leasilycrm.core.application.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.entities.Property
import com.fstengineering.leasilycrm.core.domain.helpers.CalendarMonthYear
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

interface PropertySharedCommandRepository {
    suspend fun delete(id: Long): AppResult<Unit, ApplicationError>

    suspend fun getAllMatching(matching: String?): AppResult<ImmutableList<Property>, ApplicationError>

    suspend fun getAllNeighborHoodNames(): AppResult<ImmutableList<String>, ApplicationError>

    fun watchAll(): Flow<AppResult<ImmutableList<Property>, ApplicationError>>

    suspend fun watchPropertyStaysSelectedPeriod():
            Flow<AppResult<Pair<CalendarMonthYear?, CalendarMonthYear?>, ApplicationError>>

    suspend fun updatePropertyStaysSelectedPeriod(
        selectedStartMonth: Int?,
        selectedStartYear: Int?,
        selectedEndMonth: Int?,
        selectedEndYear: Int?,
    ): AppResult<Unit, ApplicationError>
}
