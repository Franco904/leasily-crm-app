package com.fstengineering.leasilycrm.core.presentation.models

data class CalendarSelectedDate(
    val dayOfMonth: String,
    val isRangeStart: Boolean = false,
    val isRangeEnd: Boolean = false,
    val rangeSelectionLabel: Pair<RangeSelectionLabel, RangeSelectionLabel> =
        RangeSelectionLabel.None to RangeSelectionLabel.None,
)
