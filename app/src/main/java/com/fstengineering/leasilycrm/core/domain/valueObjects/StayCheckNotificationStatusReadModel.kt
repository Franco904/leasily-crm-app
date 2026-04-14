package com.fstengineering.leasilycrm.core.domain.valueObjects

data class StayCheckNotificationStatusReadModel(
    val tenantName: String,
    val propertyType: PropertyType,
    val propertyOwnerName: String,
    val hourMinute: Pair<Int, Int>,
    val mustNotifyCheckIn: Boolean,
    val mustNotifyCheckOut: Boolean,
)
