package com.fstengineering.leasilycrm.propertyDashboard.presentation.models

import com.fstengineering.leasilycrm.core.presentation.models.PropertyCalendarDateUiState

data class PropertyOccupiedPeriodDateUiState(
    val dayOfMonth: String,
    val firstHalfPeriodIndex: Int?,
    val secondHalfPeriodIndex: Int?,
    val periodId: Long,
    val tenantName: String?,
) {
    val isUnavailablePeriod = tenantName == null

    fun toCalendarState() = PropertyCalendarDateUiState(
        dayOfMonth = dayOfMonth,
        firstHalfPeriodIndex = firstHalfPeriodIndex,
        secondHalfPeriodIndex = secondHalfPeriodIndex,
        periodId = periodId,
    )
}
