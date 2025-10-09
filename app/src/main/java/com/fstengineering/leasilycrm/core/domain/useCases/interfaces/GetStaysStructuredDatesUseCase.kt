package com.fstengineering.leasilycrm.core.domain.useCases.interfaces

import com.fstengineering.leasilycrm.core.domain.models.CalendarMonthYear
import com.fstengineering.leasilycrm.core.domain.models.Stay
import com.fstengineering.leasilycrm.core.domain.models.StayCalendarDate
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.PersistentList

typealias StaysStructuredDatesMap = ImmutableMap<CalendarMonthYear, PersistentList<StayCalendarDate>>

interface GetStaysStructuredDatesUseCase {
    operator fun invoke(
        stays: List<Stay>,
    ): StaysStructuredDatesMap
}
