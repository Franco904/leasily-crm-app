package com.fstengineering.leasilycrm.occupiedPeriodDetails.application.models.dtos

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDecimal
import kotlinx.collections.immutable.ImmutableList

data class OccupiedPeriodRawReadModel(
    val id: Long,
    val propertyId: Long,
    val tenantId: Long?,
    val vehicleMake: String?,
    val vehicleColor: String?,
    val vehicleLicensePlate: String?,
    val guests: Int?,
    val checkInDateTime: AppDateTime,
    val checkInHourMinute: Pair<Int, Int>,
    val checkOutDateTime: AppDateTime,
    val checkOutHourMinute: Pair<Int, Int>,
    val isAutoCheckIn: Boolean?,
    val isAutoCheckOut: Boolean?,
    val totalPrice: AppDecimal?,
    val commission: AppDecimal?,
    val observations: String?,
    val isCheckInDone: Boolean,
    val isCheckOutDone: Boolean,
    val files: ImmutableList<StayFileReadModel>,
)
