package com.fstengineering.leasilycrm.core.domain.helpers

import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyType

data class StayCheckNotificationStatus(
    val tenantName: String,
    val propertyType: PropertyType,
    val propertyOwnerName: String,
    val hourMinute: Pair<Int, Int>,
    val mustNotifyCheckIn: Boolean,
    val mustNotifyCheckOut: Boolean,
)
