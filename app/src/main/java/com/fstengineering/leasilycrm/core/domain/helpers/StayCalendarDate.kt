package com.fstengineering.leasilycrm.core.domain.helpers

data class StayCalendarDate(
    val dayOfMonth: String,
    val firstHalfPeriodIndex: Int?,
    val secondHalfPeriodIndex: Int?,
    val stayId: Long,
)
