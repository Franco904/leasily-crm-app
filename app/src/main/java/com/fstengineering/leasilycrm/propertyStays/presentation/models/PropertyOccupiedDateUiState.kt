package com.fstengineering.leasilycrm.propertyStays.presentation.models

import com.fstengineering.leasilycrm.core.presentation.models.PropertyCalendarOccupiedDateUiState

data class PropertyOccupiedDateUiState(
    val dayOfMonth: String,
    val year: Int,
    val firstHalfPeriodIndex: Int?,
    val secondHalfPeriodIndex: Int?,
    val periodId: Long,
    val tenantName: String?,
) {
    val isUnavailablePeriod = tenantName == null

    fun toCalendarState() = PropertyCalendarOccupiedDateUiState(
        dayOfMonth = dayOfMonth,
        firstHalfPeriodIndex = firstHalfPeriodIndex,
        secondHalfPeriodIndex = secondHalfPeriodIndex,
        periodId = periodId,
    )
}
