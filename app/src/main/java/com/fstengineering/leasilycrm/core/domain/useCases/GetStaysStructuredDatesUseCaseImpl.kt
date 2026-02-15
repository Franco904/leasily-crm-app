package com.fstengineering.leasilycrm.core.domain.useCases

import com.fstengineering.leasilycrm.core.domain.models.wrappers.AppDate
import com.fstengineering.leasilycrm.core.domain.constants.UNAVAILABLE_PERIOD_INDEX
import com.fstengineering.leasilycrm.core.domain.models.helpers.CalendarMonthYear
import com.fstengineering.leasilycrm.core.domain.models.Stay
import com.fstengineering.leasilycrm.core.domain.models.helpers.StayCalendarDate
import com.fstengineering.leasilycrm.core.domain.useCases.interfaces.GetStaysStructuredDatesUseCase
import com.fstengineering.leasilycrm.core.domain.useCases.interfaces.StaysStructuredDatesMap
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap

class GetStaysStructuredDatesUseCaseImpl : GetStaysStructuredDatesUseCase {
    override operator fun invoke(
        stays: List<Stay>,
    ): StaysStructuredDatesMap {
        val staysStructuredDatesMap =
            mutableMapOf<CalendarMonthYear, PersistentList<StayCalendarDate>>()

        if (stays.isEmpty()) return staysStructuredDatesMap.toPersistentMap()

        var previousIndex: Int? = null
        var stayIndex = 0

        val sortedStays = stays.sortedBy { it.checkInDateTime.dateTime() }

        sortedStays
            .forEachIndexed { i, stay ->
                val currentIndex =
                    if (stay is Stay.UnavailablePeriod) UNAVAILABLE_PERIOD_INDEX else stayIndex

                val isLastStay = sortedStays.last() == stay
                val nextStay = if (isLastStay) null else sortedStays[sortedStays.indexOf(stay) + 1]
                val isNextStayAdjacent =
                    nextStay?.checkInDateTime?.dayOfMonth == stay.checkOutDateTime.dayOfMonth

                val nextIndex = when {
                    isLastStay || !isNextStayAdjacent -> null
                    nextStay is Stay.UnavailablePeriod -> UNAVAILABLE_PERIOD_INDEX
                    stay is Stay.UnavailablePeriod -> stayIndex
                    else -> stayIndex + 1
                }

                val stayDatesMap = getStayDatesGroupedByMonthAndYear(
                    previousIndex = previousIndex,
                    currentIndex = currentIndex,
                    nextIndex = nextIndex,
                    checkInDate = stay.checkInDateTime.toDate(),
                    checkOutDate = stay.checkOutDateTime.toDate(),
                    stayId = stay.id!!,
                )

                stayDatesMap.entries.forEach { (stayMonthYear, stayDates) ->
                    val isMonthYearPresent =
                        staysStructuredDatesMap.keys.indexOf(stayMonthYear) != -1

                    if (isMonthYearPresent) {
                        staysStructuredDatesMap[stayMonthYear] =
                            staysStructuredDatesMap[stayMonthYear]
                                ?.addAll(stayDates) ?: persistentListOf()
                    } else {
                        staysStructuredDatesMap[stayMonthYear] = stayDates
                    }
                }

                previousIndex = if (isNextStayAdjacent) currentIndex else null
                if (stay !is Stay.UnavailablePeriod) stayIndex++
            }

        return staysStructuredDatesMap
            .mapValues { it.value.toSet().toPersistentList() }
            .toPersistentMap()
    }

    /**
     * Creates a structured period selection map with the selected dates for the property stay.
     *
     * Dates are first grouped by its month/year combination, then each group values are mapped to
     * flag whether it is the period start or period end date.
     *
     * @param checkInDate The stay check-in date.
     * @param checkOutDate The stay check-out date.
     */
    private fun getStayDatesGroupedByMonthAndYear(
        previousIndex: Int?,
        currentIndex: Int,
        nextIndex: Int?,
        checkInDate: AppDate,
        checkOutDate: AppDate,
        stayId: Long,
    ): StaysStructuredDatesMap {
        val checkInDayOfMonth = checkInDate.dayOfMonth
        val checkInMonth = checkInDate.month
        val checkInYear = checkInDate.year

        val checkOutDayOfMonth = checkOutDate.dayOfMonth
        val checkOutMonth = checkOutDate.month
        val checkOutYear = checkOutDate.year

        var currentStayDate = checkInDate

        val allStayDates = mutableListOf<AppDate>()
        while (currentStayDate <= checkOutDate) {
            allStayDates.add(currentStayDate)
            currentStayDate = currentStayDate.plusDays(1)
        }

        return allStayDates
            .groupBy { stayDate ->
                CalendarMonthYear(
                    month = stayDate.month,
                    year = stayDate.year,
                )
            }
            .mapValues { (_, groupedDates) ->
                groupedDates
                    .toMutableList()
                    .map { stayDate ->
                        val isCheckInDate = stayDate.dayOfMonth == checkInDayOfMonth
                                && stayDate.month == checkInMonth
                                && stayDate.year == checkInYear

                        val isCheckOutDate = allStayDates.size > 1
                                && stayDate.dayOfMonth == checkOutDayOfMonth
                                && stayDate.month == checkOutMonth
                                && stayDate.year == checkOutYear

                        StayCalendarDate(
                            dayOfMonth = stayDate.dayOfMonth.toString(),
                            firstHalfPeriodIndex = if (isCheckInDate) previousIndex else currentIndex,
                            secondHalfPeriodIndex = if (isCheckOutDate) nextIndex else currentIndex,
                            stayId = stayId,
                        )
                    }
                    .toPersistentList()
            }
            .toImmutableMap()
    }
}
