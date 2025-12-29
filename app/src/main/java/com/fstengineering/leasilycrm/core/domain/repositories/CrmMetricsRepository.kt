package com.fstengineering.leasilycrm.core.domain.repositories

import com.fstengineering.leasilycrm.core.domain.models.CalendarMonthYear
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

interface CrmMetricsRepository {
    fun watchGeneratedLeadsPerMonth():
            Flow<AppResult<ImmutableMap<CalendarMonthYear, Int>, DataError>>

    fun watchGeneratedLeadsPerTenantSegmentPerMonth():
            Flow<AppResult<ImmutableMap<CalendarMonthYear, ImmutableMap<String, Int>>, DataError>>

    fun watchLeadContactChannelDistribution():
            Flow<AppResult<ImmutableMap<String?, Double>, DataError>>

    fun watchNewStayBookingsPerMonth():
            Flow<AppResult<ImmutableMap<CalendarMonthYear, Int>, DataError>>

    fun watchNewStayBookingsPerTenantSegmentPerMonth():
            Flow<AppResult<ImmutableMap<CalendarMonthYear, ImmutableMap<String, Int>>, DataError>>

    fun watchNewStayBookingsPerPropertyOwnerSegmentPerMonth():
            Flow<AppResult<ImmutableMap<CalendarMonthYear, ImmutableMap<String, Int>>, DataError>>

    fun watchNewStayBookingsPerTop3NeighborhoodsPerMonth():
            Flow<AppResult<ImmutableMap<CalendarMonthYear, ImmutableMap<String, Int>>, DataError>>

    fun watchCommissionInvoicePerMonth():
            Flow<AppResult<ImmutableMap<CalendarMonthYear, BigDecimal>, DataError>>

    fun watchCommissionInvoicePerTenantSegmentPerMonth():
            Flow<AppResult<ImmutableMap<CalendarMonthYear, ImmutableMap<String, BigDecimal>>, DataError>>

    fun watchCommissionInvoicePerPropertyOwnerSegmentPerMonth():
            Flow<AppResult<ImmutableMap<CalendarMonthYear, ImmutableMap<String, BigDecimal>>, DataError>>

    fun watchCommissionInvoicePerTop3NeighborhoodsPerMonth():
            Flow<AppResult<ImmutableMap<CalendarMonthYear, ImmutableMap<String, BigDecimal>>, DataError>>

    fun watchAverageTicketPerMonth():
            Flow<AppResult<ImmutableMap<CalendarMonthYear, BigDecimal>, DataError>>
}
