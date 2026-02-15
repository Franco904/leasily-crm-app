package com.fstengineering.leasilycrm.core.domain.models.helpers

data class StayCalendarDate(
    val dayOfMonth: String,
    val firstHalfPeriodIndex: Int?,
    val secondHalfPeriodIndex: Int?,
    val stayId: Long,
)
