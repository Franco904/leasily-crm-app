package com.fstengineering.leasilycrm.core.domain.repositories

import com.fstengineering.leasilycrm.core.domain.models.CalendarMonthYear
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.coroutines.flow.Flow

interface CrmMetricsRepository {
    fun watchGeneratedLeadsPerMonth():
            Flow<AppResult<ImmutableMap<CalendarMonthYear, Int>, DataSourceError>>

    fun watchGeneratedLeadsPerTenantSegmentPerMonth():
            Flow<AppResult<ImmutableMap<CalendarMonthYear, ImmutableMap<String, Int>>, DataSourceError>>

    fun watchLeadContactChannelDistribution():
            Flow<AppResult<ImmutableMap<String, Double>, DataSourceError>>
}
