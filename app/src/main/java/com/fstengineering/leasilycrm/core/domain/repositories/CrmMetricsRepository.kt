package com.fstengineering.leasilycrm.core.domain.repositories

import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDecimal
import com.fstengineering.leasilycrm.core.domain.models.CalendarMonthYear
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DataError
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.coroutines.flow.Flow

interface CrmMetricsRepository {
    fun watchGeneratedLeadsPerMonth():
            Flow<AppResult<ImmutableMap<CalendarMonthYear, Int>, DataError>>

    fun watchGeneratedLeadsPerTenantTagPerMonth():
            Flow<AppResult<ImmutableMap<CalendarMonthYear, ImmutableMap<String, Int>>, DataError>>

    fun watchLeadContactChannelDistribution():
            Flow<AppResult<ImmutableMap<String?, Double>, DataError>>

    fun watchNewStayBookingsPerMonth():
            Flow<AppResult<ImmutableMap<CalendarMonthYear, Int>, DataError>>

    fun watchNewStayBookingsPerTenantTagPerMonth():
            Flow<AppResult<ImmutableMap<CalendarMonthYear, ImmutableMap<String, Int>>, DataError>>

    fun watchNewStayBookingsPerTop3NeighborhoodsPerMonth():
            Flow<AppResult<ImmutableMap<CalendarMonthYear, ImmutableMap<String, Int>>, DataError>>

    fun watchCommissionInvoicePerMonth():
            Flow<AppResult<ImmutableMap<CalendarMonthYear, AppDecimal>, DataError>>

    fun watchCommissionInvoicePerTenantTagPerMonth():
            Flow<AppResult<ImmutableMap<CalendarMonthYear, ImmutableMap<String, AppDecimal>>, DataError>>

    fun watchCommissionInvoicePerTop3NeighborhoodsPerMonth():
            Flow<AppResult<ImmutableMap<CalendarMonthYear, ImmutableMap<String, AppDecimal>>, DataError>>

    fun watchAverageTicketPerMonth():
            Flow<AppResult<ImmutableMap<CalendarMonthYear, AppDecimal>, DataError>>
}
