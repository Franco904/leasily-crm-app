package com.fstengineering.leasilycrm.occupiedPeriodRegistration.presentation.models

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDecimal
import com.fstengineering.leasilycrm.core.presentation.models.LeadTenantUiState
import com.fstengineering.leasilycrm.core.presentation.models.PropertyCardUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

sealed interface OccupiedPeriodFormUiState {
    data object Loading : OccupiedPeriodFormUiState

    sealed interface Loaded : OccupiedPeriodFormUiState {
        val id: Long?
        val createDateTime: AppDateTime?
        val property: PropertyCardUiState?
        val checkInDateTime: AppDateTime?
        val checkOutDateTime: AppDateTime?
        val observations: String?

        val isAnyFormFieldInitialized: Boolean

        data class Stay(
            override val id: Long?,
            override val createDateTime: AppDateTime?,
            override val property: PropertyCardUiState?,
            val tenant: LeadTenantUiState?,
            val vehicleMake: String?,
            val vehicleColor: String?,
            val vehicleLicensePlate: String?,
            val guests: Int,
            override val checkInDateTime: AppDateTime?,
            val checkInTime: Pair<Int?, Int?>,
            override val checkOutDateTime: AppDateTime?,
            val checkOutTime: Pair<Int?, Int?>,
            val isAutoCheckIn: Boolean,
            val isAutoCheckOut: Boolean,
            val totalPrice: AppDecimal?,
            val commission: AppDecimal?,
            val files: ImmutableList<StayFileUiState>,
            override val observations: String?,
        ) : Loaded {
            override val isAnyFormFieldInitialized = property != null ||
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

        data class UnavailablePeriod(
            override val id: Long?,
            override val createDateTime: AppDateTime?,
            override val property: PropertyCardUiState?,
            override val checkInDateTime: AppDateTime?,
            override val checkOutDateTime: AppDateTime?,
            override val observations: String?,
        ) : Loaded {
            override val isAnyFormFieldInitialized = property != null ||
                    checkInDateTime != null ||
                    checkOutDateTime != null ||
                    observations != null
        }
    }
}
