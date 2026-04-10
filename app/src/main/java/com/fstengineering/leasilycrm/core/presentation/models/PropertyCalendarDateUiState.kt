package com.fstengineering.leasilycrm.core.presentation.models

data class PropertyCalendarDateUiState(
    val dayOfMonth: String,
    val firstHalfPeriodIndex: Int?,
    val secondHalfPeriodIndex: Int?,
    val periodId: Long,
)
