package com.fstengineering.leasilycrm.core.domain.repositories

import com.fstengineering.leasilycrm.core.domain.helpers.CalendarMonthYear
import com.fstengineering.leasilycrm.core.domain.entities.Property
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

interface PropertyRepository {
    suspend fun insertOrUpdate(property: Property): AppResult<Unit, DataError>

    suspend fun delete(id: Long): AppResult<Unit, DataError>

    suspend fun getById(propertyId: Long): AppResult<Property, DataError>

    suspend fun getAll(): AppResult<ImmutableList<Property>, DataError>

    suspend fun getAllMatching(matching: String?): AppResult<ImmutableList<Property>, DataError>

    suspend fun getAllNeighborHoodNames(): AppResult<ImmutableList<String>, DataError>

    fun watchById(propertyId: Long): Flow<AppResult<Property?, DataError>>

    fun watchAll(): Flow<AppResult<ImmutableList<Property>, DataError>>

    suspend fun watchPropertyStaysSelectedPeriod():
            Flow<AppResult<Pair<CalendarMonthYear?, CalendarMonthYear?>, DataError>>

    suspend fun updatePropertyStaysSelectedPeriod(
        selectedStartMonth: Int?,
        selectedStartYear: Int?,
        selectedEndMonth: Int?,
        selectedEndYear: Int?,
    ): AppResult<Unit, DataError>
}
