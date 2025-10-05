package com.fstengineering.leasilycrm.core.domain.models

data class StayCalendarDate(
    val dayOfMonth: String,
    val isCheckInDate: Boolean,
    val isCheckOutDate: Boolean,
    val isUnavailableDate: Boolean,
)