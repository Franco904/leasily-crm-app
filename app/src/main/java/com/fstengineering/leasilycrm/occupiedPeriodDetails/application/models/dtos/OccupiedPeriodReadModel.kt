package com.fstengineering.leasilycrm.occupiedPeriodDetails.application.models.dtos

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDecimal
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppYearMonth
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyOccupiedDate
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap

data class OccupiedPeriodReadModel(
    val id: Long,
    val tenantId: Long?,
    val property: PropertyReadModel,
    val tenant: TenantReadModel?,
    val vehicleMake: String?,
    val vehicleColor: String?,
    val vehicleLicensePlate: String?,
    val guests: Int?,
    val checkInDateTime: AppDateTime,
    val checkInHourMinute: Pair<Int, Int>,
    val checkOutDateTime: AppDateTime,
    val checkOutHourMinute: Pair<Int, Int>,
    val isAutoCheckIn: Boolean,
    val isAutoCheckOut: Boolean,
    val totalPrice: AppDecimal?,
    val commission: AppDecimal?,
    val observations: String?,
    val isCheckInDone: Boolean,
    val isCheckOutDone: Boolean,
    val files: ImmutableList<StayFileReadModel>,
    val occupiedDatesGrouped: ImmutableMap<AppYearMonth, ImmutableList<PropertyOccupiedDate>>,
) {
    val isUnavailablePeriod get() = tenantId == null

    companion object {
        fun create(
            raw: OccupiedPeriodRawReadModel,
            property: PropertyReadModel,
            tenant: TenantReadModel?,
            occupiedDatesGrouped: ImmutableMap<AppYearMonth, ImmutableList<PropertyOccupiedDate>>,
        ) = OccupiedPeriodReadModel(
            id = raw.id,
            tenantId = raw.tenantId,
            property = property,
            tenant = tenant,
            vehicleMake = raw.vehicleMake,
            vehicleColor = raw.vehicleColor,
            vehicleLicensePlate = raw.vehicleLicensePlate,
            guests = raw.guests,
            checkInDateTime = raw.checkInDateTime,
            checkInHourMinute = raw.checkInHourMinute,
            checkOutDateTime = raw.checkOutDateTime,
            checkOutHourMinute = raw.checkOutHourMinute,
            isAutoCheckIn = raw.isAutoCheckIn ?: false,
            isAutoCheckOut = raw.isAutoCheckOut ?: false,
            totalPrice = raw.totalPrice,
            commission = raw.commission,
            observations = raw.observations,
            isCheckInDone = raw.isCheckInDone,
            isCheckOutDone = raw.isCheckOutDone,
            files = raw.files,
            occupiedDatesGrouped = occupiedDatesGrouped,
        )
    }
}
