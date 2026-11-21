package com.fstengineering.leasilycrm.occupiedPeriodDetails.presentation.models

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDecimal
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppYearMonth
import com.fstengineering.leasilycrm.core.presentation.models.ClienteUiState
import com.fstengineering.leasilycrm.core.presentation.models.ImovelCalendarOccupiedDateUiState
import com.fstengineering.leasilycrm.core.presentation.models.ImovelCardUiState
import com.fstengineering.leasilycrm.core.presentation.models.StayTransferUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap

sealed interface OccupiedPeriodDetailsUiState {
    data object Loading : OccupiedPeriodDetailsUiState

    data class Loaded(
        val id: Long,
        val tenantId: Long?,
        val imovel: ImovelCardUiState,
        val tenant: ClienteUiState?,
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
        val files: ImmutableList<StayFileUiState>,
        val transfers: ImmutableList<StayTransferUiState>,
        val occupiedDatesGrouped: ImmutableMap<AppYearMonth, ImmutableList<ImovelCalendarOccupiedDateUiState>>,
    ) : OccupiedPeriodDetailsUiState {
        val isUnavailablePeriod get() = tenantId == null
    }
}
