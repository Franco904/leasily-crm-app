package com.fstengineering.leasilycrm.core.domain.repositories

import com.fstengineering.leasilycrm.core.domain.models.CalendarMonthYear
import com.fstengineering.leasilycrm.core.domain.models.Property
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

interface PropertyRepository {
    suspend fun insertOrUpdate(property: Property): AppResult<Long, DataSourceError>

    suspend fun delete(id: Long): AppResult<Unit, DataSourceError>

    suspend fun getById(propertyId: Long): AppResult<Property, DataSourceError>

    suspend fun getAll(): AppResult<ImmutableList<Property>, DataSourceError>

    suspend fun getAllMatching(matching: String?): AppResult<ImmutableList<Property>, DataSourceError>

    suspend fun getAllNeighborHoodNames(): AppResult<ImmutableList<String>, DataSourceError>

    fun watchById(propertyId: Long): Flow<AppResult<Property?, DataSourceError>>

    fun watchAll(): Flow<AppResult<ImmutableList<Property>, DataSourceError>>

    suspend fun watchPropertyStaysSelectedPeriod():
            Flow<AppResult<Pair<CalendarMonthYear?, CalendarMonthYear?>, DataSourceError>>

    suspend fun updatePropertyStaysSelectedPeriod(
        selectedStartMonth: Int?,
        selectedStartYear: Int?,
        selectedEndMonth: Int?,
        selectedEndYear: Int?,
    ): AppResult<Unit, DataSourceError>
}
