package com.fstengineering.leasilycrm.core.domain.models.helpers

data class StayCheckNotificationStatus(
    val tenantName: String,
    val propertyTypeName: String,
    val propertyOwnerName: String,
    val hourMinute: Pair<Int, Int>,
    val mustNotifyCheckIn: Boolean,
    val mustNotifyCheckOut: Boolean,
)
