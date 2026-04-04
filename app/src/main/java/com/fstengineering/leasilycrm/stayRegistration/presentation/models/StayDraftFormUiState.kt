package com.fstengineering.leasilycrm.stayRegistration.presentation.models

import com.fstengineering.leasilycrm.core.domain.helpers.AppDateTime
import com.fstengineering.leasilycrm.core.domain.helpers.AppDecimal
import com.fstengineering.leasilycrm.core.presentation.models.LeadTenantUiState
import com.fstengineering.leasilycrm.core.presentation.models.PropertyUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

sealed interface StayDraftFormUiState {
    data object Loading : StayDraftFormUiState

    data class StayOwnedLoaded(
        val id: Long?,
        val createDateTime: AppDateTime?,
        val property: PropertyUiState?,
        val tenant: LeadTenantUiState?,
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
        val files: ImmutableList<StayFileUiState>,
        val observations: String?,
    ) : StayDraftFormUiState {
        val isAnyFormFieldInitialized = property != null ||
                tenant != null ||
                vehicleMake != null ||
                vehicleColor != null ||
                vehicleLicensePlate != null ||
                checkInDateTime != null ||
                checkInTime.first != null ||
                checkInTime.second != null ||
                checkOutDateTime != null ||
                checkOutTime.first != null ||
                checkOutTime.second != null ||
                isAutoCheckIn ||
                isAutoCheckOut ||
                totalPrice != null ||
                commission != null ||
                files.isNotEmpty() ||
                observations != null

        fun addFile(newFile: StayFileUiState) = copy(
            files = (files + newFile).toPersistentList(),
        )
    }

    data class UnavailablePeriodLoaded(
        val id: Long?,
        val createDateTime: AppDateTime?,
        val property: PropertyUiState?,
        val checkInDateTime: AppDateTime?,
        val checkOutDateTime: AppDateTime?,
        val observations: String?,
    ) : StayDraftFormUiState {
        val isAnyFormFieldInitialized = property != null ||
                checkInDateTime != null ||
                checkOutDateTime != null ||
                observations != null
    }
}
