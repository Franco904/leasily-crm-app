package com.fstengineering.leasilycrm.core.presentation.models

data class PropertyCalendarOccupiedDateUiState(
    val dayOfMonth: String,
    val firstHalfPeriodIndex: Int?,
    val secondHalfPeriodIndex: Int?,
    val periodId: Long,
)
