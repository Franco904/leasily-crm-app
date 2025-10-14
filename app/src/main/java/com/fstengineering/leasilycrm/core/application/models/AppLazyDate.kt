package com.fstengineering.leasilycrm.core.application.models

import com.fstengineering.leasilycrm.core.domain.models.CalendarMonthYear
import com.fstengineering.leasilycrm.core.domain.models.StayCalendarDate
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap
import java.time.LocalDate

data class AppLazyDate(
    val date: () -> LocalDate,
) {
    fun withMonthYear(month: Int, year: Int): AppLazyDate {
        val updatedDate = date().withMonth(month).withYear(year)
        return copy(date = { updatedDate })
    }

    fun getLast12Months(): ImmutableList<CalendarMonthYear> {
        val last12MonthsMap = mutableListOf<CalendarMonthYear>()

        repeat(12) { i ->
            val pastDate = date().minusMonths(i.toLong())

            val calendarMonthYear = CalendarMonthYear(
                month = pastDate.monthValue,
                year = pastDate.year,
            )

            last12MonthsMap.add(calendarMonthYear)
        }

        return last12MonthsMap.toPersistentList()
    }

    fun isAfter(other: AppLazyDate?): Boolean {
        return other == null || date() >= other.date()
    }

    fun isBefore(other: AppLazyDate?): Boolean {
        return other == null || date() <= other.date()
    }

    companion object {
        fun now(): AppLazyDate = AppLazyDate { LocalDate.now() }

        fun getDatesWithMissingMonthsBetween(
            startDate: AppLazyDate,
            endDate: AppLazyDate,
            staysStructuredDatesMap: Map<CalendarMonthYear, PersistentList<StayCalendarDate>>,
        ): ImmutableMap<CalendarMonthYear, PersistentList<StayCalendarDate>> {
            val staysStructuredDatesWithMissingMonthsMap =
                mutableMapOf<CalendarMonthYear, PersistentList<StayCalendarDate>>()

            var currentDate = startDate.date()
            while (currentDate <= endDate.date()) {
                val calendarMonthYear = CalendarMonthYear(
                    month = currentDate.monthValue,
                    year = currentDate.year,
                )

                staysStructuredDatesWithMissingMonthsMap.put(calendarMonthYear, persistentListOf())
                currentDate = currentDate.plusMonths(1)
            }

            staysStructuredDatesWithMissingMonthsMap.keys.forEach { monthYear ->
                val stayDates = staysStructuredDatesMap[monthYear]

                staysStructuredDatesWithMissingMonthsMap.put(
                    monthYear,
                    stayDates ?: persistentListOf()
                )
            }

            return staysStructuredDatesWithMissingMonthsMap
                .toPersistentMap()
        }
    }
}
