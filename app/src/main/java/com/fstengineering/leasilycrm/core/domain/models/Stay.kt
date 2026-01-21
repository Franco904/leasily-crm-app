package com.fstengineering.leasilycrm.core.domain.models

import com.fstengineering.leasilycrm.core.application.models.AppDateTime
import com.fstengineering.leasilycrm.core.application.models.AppDecimal
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class Stay(
    val id: Long? = null,
    val createDateTime: AppDateTime? = null,
    val updateDateTime: AppDateTime? = null,
    val isUnavailablePeriod: Boolean = false,
    val qtGuests: Int? = 1,
    val checkInDateTime: AppDateTime? = null,
    val checkInHourMinute: Pair<Int?, Int?> = null to null,
    val checkOutDateTime: AppDateTime? = null,
    val checkOutHourMinute: Pair<Int?, Int?> = null to null,
    val isAutoCheckIn: Boolean = false,
    val isAutoCheckOut: Boolean = false,
    val totalPrice: AppDecimal? = null,
    val commission: AppDecimal? = null,
    val vehicleMake: String? = null,
    val vehicleColor: String? = null,
    val vehicleLicensePlate: String? = null,
    val observations: String? = null,
    val isCheckInDone: Boolean = false,
    val isCheckOutDone: Boolean = false,
    val isDeleted: Boolean = false,
    val property: Property? = null,
    val tenant: Tenant? = null,
    val transfers: ImmutableList<StayTransfer> = persistentListOf(),
    val files: ImmutableList<StayFile> = persistentListOf(),
    val userId: Long? = null,
) {
    val isAnyFormFieldInitialized = property != null
            || tenant != null
            || qtGuests != null
            || vehicleMake != null
            || vehicleColor != null
            || vehicleLicensePlate != null
            || checkInDateTime != null || checkInHourMinute != null
            || checkOutDateTime != null || checkOutHourMinute != null
            || isAutoCheckIn || isAutoCheckOut
            || totalPrice != null || commission != null
            || transfers.isNotEmpty()
            || files.isNotEmpty()
            || observations != null

}
