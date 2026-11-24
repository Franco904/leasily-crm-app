package com.fstengineering.leasilycrm.dashboard.application.repositories

import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDecimal
import com.fstengineering.leasilycrm.core.domain.valueObjects.CalendarMonthYear
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.coroutines.flow.Flow

interface UserAnalyticsRepository {
    fun watchGeneratedLeadsPerMonth():
        Flow<AppResult<ImmutableMap<CalendarMonthYear, Int>>>

    fun watchGeneratedLeadsPerTenantTagPerMonth():
        Flow<AppResult<ImmutableMap<CalendarMonthYear, ImmutableMap<String, Int>>>>

    fun watchLeadContactChannelDistribution():
        Flow<AppResult<ImmutableMap<String?, Double>>>

    fun watchNewStayBookingsPerMonth():
        Flow<AppResult<ImmutableMap<CalendarMonthYear, Int>>>

    fun watchNewStayBookingsPerTenantTagPerMonth():
        Flow<AppResult<ImmutableMap<CalendarMonthYear, ImmutableMap<String, Int>>>>

    fun watchNewStayBookingsPerTop3NeighborhoodsPerMonth():
        Flow<AppResult<ImmutableMap<CalendarMonthYear, ImmutableMap<String, Int>>>>

    fun watchCommissionInvoicePerMonth():
        Flow<AppResult<ImmutableMap<CalendarMonthYear, AppDecimal>>>

    fun watchCommissionInvoicePerTenantTagPerMonth():
        Flow<AppResult<ImmutableMap<CalendarMonthYear, ImmutableMap<String, AppDecimal>>>>

    fun watchCommissionInvoicePerTop3NeighborhoodsPerMonth():
        Flow<AppResult<ImmutableMap<CalendarMonthYear, ImmutableMap<String, AppDecimal>>>>

    fun watchAverageTicketPerMonth():
        Flow<AppResult<ImmutableMap<CalendarMonthYear, AppDecimal>>>
}
