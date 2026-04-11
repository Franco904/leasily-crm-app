package com.fstengineering.leasilycrm.core.domain.valueObjects

data class StayCalendarDate(
    val dayOfMonth: String,
    val firstHalfPeriodIndex: Int?,
    val secondHalfPeriodIndex: Int?,
    val stayId: Long,
)
