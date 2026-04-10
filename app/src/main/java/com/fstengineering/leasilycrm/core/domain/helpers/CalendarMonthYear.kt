package com.fstengineering.leasilycrm.core.domain.helpers

data class CalendarMonthYear(
    val month: Int = 0,
    val year: Int = 0,
) {
    val id: Int = (month * 100) + year
}
