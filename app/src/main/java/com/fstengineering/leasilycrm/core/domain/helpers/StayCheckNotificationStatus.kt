package com.fstengineering.leasilycrm.core.domain.helpers

data class StayCheckNotificationStatus(
    val tenantName: String,
    val propertyTypeId: Long,
    val propertyOwnerName: String,
    val hourMinute: Pair<Int, Int>,
    val mustNotifyCheckIn: Boolean,
    val mustNotifyCheckOut: Boolean,
)
