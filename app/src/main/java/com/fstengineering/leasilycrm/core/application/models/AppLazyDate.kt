package com.fstengineering.leasilycrm.core.application.models

import com.fstengineering.leasilycrm.core.domain.models.CalendarMonthYear
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import java.time.LocalDate

data class AppLazyDate(
    val date: () -> LocalDate,
) {
    fun getLast12Months(): ImmutableList<CalendarMonthYear> {
        val last12MonthsMap = mutableListOf<CalendarMonthYear>()

        repeat(12) { i ->
            val pastDate = date().minusMonths(i.toLong())

            val calendarMonthYear = CalendarMonthYear(
                id = pastDate.monthValue + pastDate.year,
                month = pastDate.monthValue,
                year = pastDate.year,
            )

            last12MonthsMap.add(calendarMonthYear)
        }

        return last12MonthsMap.toPersistentList()
    }

    companion object {
        fun now(): AppLazyDate = AppLazyDate { LocalDate.now() }
    }
}
