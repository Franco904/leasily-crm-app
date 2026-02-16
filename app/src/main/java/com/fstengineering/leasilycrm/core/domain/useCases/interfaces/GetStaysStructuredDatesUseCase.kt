package com.fstengineering.leasilycrm.core.domain.useCases.interfaces

import com.fstengineering.leasilycrm.core.domain.helpers.CalendarMonthYear
import com.fstengineering.leasilycrm.core.domain.entities.Stay
import com.fstengineering.leasilycrm.core.domain.helpers.StayCalendarDate
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.PersistentList

typealias StaysStructuredDatesMap = ImmutableMap<CalendarMonthYear, PersistentList<StayCalendarDate>>

interface GetStaysStructuredDatesUseCase {
    operator fun invoke(
        stays: List<Stay>,
    ): StaysStructuredDatesMap
}
