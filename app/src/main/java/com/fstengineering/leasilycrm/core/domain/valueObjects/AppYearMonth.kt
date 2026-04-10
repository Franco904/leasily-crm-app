package com.fstengineering.leasilycrm.core.domain.valueObjects

import java.time.YearMonth

data class AppYearMonth(private val yearMonth: YearMonth) {
    val year get() = yearMonth.year
    val month get() = yearMonth.monthValue

    override fun equals(other: Any?): Boolean {
        if (other !is AppYearMonth) return false

        return yearMonth == other.yearMonth
    }

    override fun hashCode(): Int = yearMonth.hashCode()

    companion object {
        fun of(year: Int, month: Int) = AppYearMonth(YearMonth.of(year, month))
    }
}