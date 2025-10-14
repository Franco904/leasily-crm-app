package com.fstengineering.leasilycrm.core.domain.models

data class CalendarMonthYear(
    val month: Int = 0,
    val year: Int = 0,
) {
    val id: Int = month + year
}
