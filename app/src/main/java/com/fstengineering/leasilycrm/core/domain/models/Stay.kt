package com.fstengineering.leasilycrm.core.domain.models

import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDateTime
import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDecimal
import kotlinx.collections.immutable.ImmutableList

sealed class Stay(
    open val id: Long?,
    open val createDateTime: AppDateTime?,
    open val updateDateTime: AppDateTime?,
    open val property: StayProperty,
    open val checkInDateTime: AppDateTime,
    open val checkOutDateTime: AppDateTime,
    open val observations: String?,
    open val isDeleted: Boolean,
    open val userId: Long,
) {
    abstract fun copyWithUserId(userId: Long): Stay

    data class Owned(
        override val id: Long?,
        override val createDateTime: AppDateTime?,
        override val updateDateTime: AppDateTime?,
        override val property: StayProperty,
        override val checkInDateTime: AppDateTime,
        override val checkOutDateTime: AppDateTime,
        override val observations: String?,
        override val isDeleted: Boolean,
        override val userId: Long,
        val tenant: StayOwnedTenant,
        val vehicleMake: String?,
        val vehicleColor: String?,
        val vehicleLicensePlate: String?,
        val qtGuests: Int,
        val checkInHourMinute: Pair<Int, Int>,
        val checkOutHourMinute: Pair<Int, Int>,
        val isAutoCheckIn: Boolean,
        val isAutoCheckOut: Boolean,
        val totalPrice: AppDecimal?,
        val commission: AppDecimal,
        val isCheckInDone: Boolean,
        val isCheckOutDone: Boolean,
        val transfers: ImmutableList<StayTransfer>,
        val files: ImmutableList<StayFile>,
    ) : Stay(
        id = id,
        createDateTime = createDateTime,
        updateDateTime = updateDateTime,
        property = property,
        checkInDateTime = checkInDateTime,
        checkOutDateTime = checkOutDateTime,
        observations = observations,
        isDeleted = isDeleted,
        userId = userId,
    ) {
        override fun copyWithUserId(userId: Long): Stay = copy(userId = userId)
    }

    data class UnavailablePeriod(
        override val id: Long?,
        override val createDateTime: AppDateTime?,
        override val updateDateTime: AppDateTime?,
        override val property: StayProperty,
        override val checkInDateTime: AppDateTime,
        override val checkOutDateTime: AppDateTime,
        override val observations: String?,
        override val isDeleted: Boolean,
        override val userId: Long,
    ) : Stay(
        id = id,
        createDateTime = createDateTime,
        updateDateTime = updateDateTime,
        property = property,
        checkInDateTime = checkInDateTime,
        checkOutDateTime = checkOutDateTime,
        observations = observations,
        isDeleted = isDeleted,
        userId = userId,
    ) {
        override fun copyWithUserId(userId: Long): Stay = copy(userId = userId)
    }
}

//val isAnyFormFieldInitialized = property != null
//        || tenant != null
//        || vehicleMake != null
//        || vehicleColor != null
//        || vehicleLicensePlate != null
//        || qtGuests != null
//        || checkInDateTime != null || checkInHourMinute != null
//        || checkOutDateTime != null || checkOutHourMinute != null
//        || isAutoCheckIn || isAutoCheckOut
//        || totalPrice != null || commission != null
//        || transfers.isNotEmpty()
//        || files.isNotEmpty()
//        || observations != null
