package com.fstengineering.leasilycrm.occupiedPeriods.application.models.dtos

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyType

data class OccupiedPeriodListReadModel(
    val id: Long,
    val tenantId: Long?,
    val tenantName: String?,
    val propertyType: PropertyType?,
    val propertyNeighborhood: String?,
    val checkInDateTime: AppDateTime,
    val checkInHourMinute: Pair<Int, Int>,
    val checkOutDateTime: AppDateTime,
    val checkOutHourMinute: Pair<Int, Int>,
    val isAutoCheckIn: Boolean,
    val isAutoCheckOut: Boolean,
    val isCheckInDone: Boolean,
    val isCheckOutDone: Boolean,
) {
    val isUnavailablePeriod get() = tenantId == null
}
