package com.fstengineering.leasilycrm.core.presentation.utils.interfaces

import com.fstengineering.leasilycrm.core.presentation.models.CalendarMonthYear
import com.fstengineering.leasilycrm.core.presentation.models.CalendarSelectedDate
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import java.time.LocalDate

typealias ImmutableSelectedDates = ImmutableMap<CalendarMonthYear, ImmutableList<CalendarSelectedDate>>

interface CalendarSelectionUtils {
    fun getNewSelectedDates(
        startDate: LocalDate,
        endDate: LocalDate,
        currentRangeCount: Int,
        currentSelectedDates: ImmutableSelectedDates,
    ): ImmutableSelectedDates
}
