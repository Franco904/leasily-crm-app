package com.fstengineering.leasilycrm.dashboard.application.repositories

import com.fstengineering.leasilycrm.core.application.utils.ApplicationError
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDecimal
import com.fstengineering.leasilycrm.core.domain.valueObjects.CalendarMonthYear
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.coroutines.flow.Flow

interface UserAnalyticsRepository {
    fun watchGeneratedLeadsPerMonth():
            Flow<AppResult<ImmutableMap<CalendarMonthYear, Int>, ApplicationError>>

    fun watchGeneratedLeadsPerTenantTagPerMonth():
            Flow<AppResult<ImmutableMap<CalendarMonthYear, ImmutableMap<String, Int>>, ApplicationError>>

    fun watchLeadContactChannelDistribution():
            Flow<AppResult<ImmutableMap<String?, Double>, ApplicationError>>

    fun watchNewStayBookingsPerMonth():
            Flow<AppResult<ImmutableMap<CalendarMonthYear, Int>, ApplicationError>>

    fun watchNewStayBookingsPerTenantTagPerMonth():
            Flow<AppResult<ImmutableMap<CalendarMonthYear, ImmutableMap<String, Int>>, ApplicationError>>

    fun watchNewStayBookingsPerTop3NeighborhoodsPerMonth():
            Flow<AppResult<ImmutableMap<CalendarMonthYear, ImmutableMap<String, Int>>, ApplicationError>>

    fun watchCommissionInvoicePerMonth():
            Flow<AppResult<ImmutableMap<CalendarMonthYear, AppDecimal>, ApplicationError>>

    fun watchCommissionInvoicePerTenantTagPerMonth():
            Flow<AppResult<ImmutableMap<CalendarMonthYear, ImmutableMap<String, AppDecimal>>, ApplicationError>>

    fun watchCommissionInvoicePerTop3NeighborhoodsPerMonth():
            Flow<AppResult<ImmutableMap<CalendarMonthYear, ImmutableMap<String, AppDecimal>>, ApplicationError>>

    fun watchAverageTicketPerMonth():
            Flow<AppResult<ImmutableMap<CalendarMonthYear, AppDecimal>, ApplicationError>>
}
