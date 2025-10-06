package com.fstengineering.leasilycrm.core.domain.useCases.interfaces

import com.fstengineering.leasilycrm.core.domain.models.CalendarMonthYear
import com.fstengineering.leasilycrm.core.domain.models.Stay
import com.fstengineering.leasilycrm.core.domain.models.StayCalendarDate
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap

typealias StaysStructuredDatesMap = ImmutableMap<CalendarMonthYear, ImmutableList<StayCalendarDate>>

interface GetStaysStructuredDatesUseCase {
    operator fun invoke(
        stays: ImmutableList<Stay>,
    ): StaysStructuredDatesMap
}
