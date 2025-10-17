package com.fstengineering.leasilycrm.core.domain.repositories

import com.fstengineering.leasilycrm.core.domain.models.CalendarMonthYear
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataSourceError
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

interface CrmMetricsRepository {
    fun watchGeneratedLeadsPerMonth():
            Flow<AppResult<ImmutableMap<CalendarMonthYear, Int>, DataSourceError>>

    fun watchGeneratedLeadsPerTenantSegmentPerMonth():
            Flow<AppResult<ImmutableMap<CalendarMonthYear, ImmutableMap<String, Int>>, DataSourceError>>

    fun watchLeadContactChannelDistribution():
            Flow<AppResult<ImmutableMap<String?, Double>, DataSourceError>>

    fun watchNewStayBookingsPerMonth():
            Flow<AppResult<ImmutableMap<CalendarMonthYear, Int>, DataSourceError>>

    fun watchNewStayBookingsPerTenantSegmentPerMonth():
            Flow<AppResult<ImmutableMap<CalendarMonthYear, ImmutableMap<String, Int>>, DataSourceError>>

    fun watchNewStayBookingsPerPropertyOwnerSegmentPerMonth():
            Flow<AppResult<ImmutableMap<CalendarMonthYear, ImmutableMap<String, Int>>, DataSourceError>>

    fun watchNewStayBookingsPerTop3NeighborhoodsPerMonth():
            Flow<AppResult<ImmutableMap<CalendarMonthYear, ImmutableMap<String, Int>>, DataSourceError>>

    fun watchCommissionInvoicePerMonth():
            Flow<AppResult<ImmutableMap<CalendarMonthYear, BigDecimal>, DataSourceError>>

    fun watchCommissionInvoicePerTenantSegmentPerMonth():
            Flow<AppResult<ImmutableMap<CalendarMonthYear, ImmutableMap<String, BigDecimal>>, DataSourceError>>

    fun watchCommissionInvoicePerPropertyOwnerSegmentPerMonth():
            Flow<AppResult<ImmutableMap<CalendarMonthYear, ImmutableMap<String, BigDecimal>>, DataSourceError>>

    fun watchCommissionInvoicePerTop3NeighborhoodsPerMonth():
            Flow<AppResult<ImmutableMap<CalendarMonthYear, ImmutableMap<String, BigDecimal>>, DataSourceError>>

    fun watchAverageTicketPerMonth():
            Flow<AppResult<ImmutableMap<CalendarMonthYear, BigDecimal>, DataSourceError>>
}
