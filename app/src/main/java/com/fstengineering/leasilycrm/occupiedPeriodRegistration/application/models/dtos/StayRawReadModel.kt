package com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.dtos

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDecimal
import kotlinx.collections.immutable.ImmutableList

data class StayRawReadModel(
    val id: Long?,
    val createDateTime: AppDateTime?,
    val propertyId: Long,
    val tenantId: Long,
    val vehicleMake: String?,
    val vehicleColor: String?,
    val vehicleLicensePlate: String?,
    val guests: Int,
    val checkInDateTime: AppDateTime?,
    val checkInTime: Pair<Int?, Int?>,
    val checkOutDateTime: AppDateTime?,
    val checkOutTime: Pair<Int?, Int?>,
    val isAutoCheckIn: Boolean,
    val isAutoCheckOut: Boolean,
    val totalPrice: AppDecimal?,
    val commission: AppDecimal?,
    val files: ImmutableList<StayFileReadModel>,
    val observations: String?,
)
