package com.fstengineering.leasilycrm.stayDetails.model

import com.fstengineering.leasilycrm.core.domain.models.Property
import com.fstengineering.leasilycrm.core.domain.models.Saved
import com.fstengineering.leasilycrm.core.domain.models.Stay
import com.fstengineering.leasilycrm.core.domain.models.StayFileWithName
import com.fstengineering.leasilycrm.core.domain.models.StayTransfer
import com.fstengineering.leasilycrm.core.domain.models.TenantSaved
import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDateTime
import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDecimal
import kotlinx.collections.immutable.ImmutableList

sealed interface StayDetailsUiState {
    data object Empty : StayDetailsUiState

    data class Filled(
        val stay: Stay,
        val id: Long,
        val createDateTime: AppDateTime?,
        val updateDateTime: AppDateTime?,
        val property: Property.Saved,
        val tenant: TenantSaved?,
        val vehicleMake: String?,
        val vehicleColor: String?,
        val vehicleLicensePlate: String?,
        val qtGuests: Int,
        val checkInDateTime: AppDateTime,
        val checkInHourMinute: Pair<Int?, Int?>,
        val checkOutDateTime: AppDateTime,
        val checkOutHourMinute: Pair<Int?, Int?>,
        val isAutoCheckIn: Boolean,
        val isAutoCheckOut: Boolean,
        val totalPrice: AppDecimal?,
        val commission: AppDecimal,
        val observations: String?,
        val isCheckInDone: Boolean,
        val isCheckOutDone: Boolean,
        val isUnavailablePeriod: Boolean,
        val isDeleted: Boolean,
        val transfers: ImmutableList<StayTransfer>,
        val files: ImmutableList<StayFileWithName>,
        val userId: Long,
    ) : StayDetailsUiState
}
