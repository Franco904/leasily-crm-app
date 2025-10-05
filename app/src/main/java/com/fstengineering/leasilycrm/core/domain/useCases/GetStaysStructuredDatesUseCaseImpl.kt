package com.fstengineering.leasilycrm.core.domain.useCases

import com.fstengineering.leasilycrm.core.domain.models.Stay
import com.fstengineering.leasilycrm.core.domain.useCases.interfaces.GetStaysStructuredDatesUseCase
import com.fstengineering.leasilycrm.core.domain.useCases.interfaces.StaysStructuredDatesMap
import com.fstengineering.leasilycrm.core.domain.models.CalendarMonthYear
import com.fstengineering.leasilycrm.core.domain.models.StayCalendarDate
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.collections.immutable.toPersistentList
import java.time.LocalDate

class GetStaysStructuredDatesUseCaseImpl : GetStaysStructuredDatesUseCase {
    override operator fun invoke(
        stays: ImmutableList<Stay>,
//        currentSelectedDates: ImmutableSelectedDates,
    ): StaysStructuredDatesMap {
        if (stays.isEmpty()) return persistentMapOf()

        val firstStay = stays.first()

        val staysStructuredDatesMap = getStayDatesGroupedByMonthAndYear(
            checkInDate = firstStay.checkInDateTime?.toLocalDate()!!,
            checkOutDate = firstStay.checkOutDateTime?.toLocalDate()!!,
            isUnavailablePeriod = firstStay.isUnavailablePeriod,
        )
//            .mergeToExistingDates(currentSelectedDates = currentSelectedDates)
//            .removeDuplicatedDates()

        return staysStructuredDatesMap
    }

    /**
     * Creates a structured period selection map with the selected dates for the property stay.
     *
     * Dates are first grouped by its month/year combination, then each group values are mapped to
     * flag whether it is the period start or period end date. The date's color and whether it
     * belongs to an unavailable stay period are also mapped.
     *
     * @param checkInDate The stay check-in date.
     * @param checkOutDate The stay check-out date.
     * @param isUnavailablePeriod Whether the stay dates belong to an unavailable period.
     */
    private fun getStayDatesGroupedByMonthAndYear(
        checkInDate: LocalDate,
        checkOutDate: LocalDate,
        isUnavailablePeriod: Boolean,
    ): StaysStructuredDatesMap {
        val checkInDayOfMonth = checkInDate.dayOfMonth
        val checkInMonth = checkInDate.monthValue
        val checkInYear = checkInDate.year

        val checkOutDayOfMonth = checkOutDate.dayOfMonth
        val checkOutMonth = checkOutDate.monthValue
        val checkOutYear = checkOutDate.year

        var currentStayDate = checkInDate

        val allStayDates = mutableListOf<LocalDate>()
        while (currentStayDate <= checkOutDate) {
            allStayDates.add(currentStayDate)
            currentStayDate = currentStayDate.plusDays(1)
        }

        return allStayDates
            .groupBy { stayDate ->
                CalendarMonthYear(
                    id = stayDate.month.value + stayDate.year,
                    month = stayDate.month.value,
                    year = stayDate.year,
                )
            }
            .mapValues { (_, groupedDates) ->
                groupedDates
                    .toMutableList()
                    .map { stayDate ->
                        val isCheckInDate = stayDate.dayOfMonth == checkInDayOfMonth
                                && stayDate.month.value == checkInMonth
                                && stayDate.year == checkInYear

                        val isCheckOutDate = allStayDates.size > 1
                                && stayDate.dayOfMonth == checkOutDayOfMonth
                                && stayDate.month.value == checkOutMonth
                                && stayDate.year == checkOutYear

                        StayCalendarDate(
                            dayOfMonth = stayDate.dayOfMonth.toString(),
                            isCheckInDate = isCheckInDate,
                            isCheckOutDate = isCheckOutDate,
                            isUnavailableDate = isUnavailablePeriod,
                        )
                    }
                    .toImmutableList()
            }
            .toImmutableMap()
    }
}

/**
 * Merge dates values to already existing dates, looking for repeated calendar month/year keys,
 * both containing values for that month/year combination.
 *
 * Duplicated calendar month/year keys may occur when there is already a range selection,
 * and the user adds a new date range, starting from the last month/year of the current date
 * range.
 *
 * @param currentSelectedDates The current range selection dates map for merge, that will be
 * used to obtain the already existing date values for a month/year.
 */
private fun StaysStructuredDatesMap.mergeToExistingDates(
    currentSelectedDates: StaysStructuredDatesMap,
): StaysStructuredDatesMap {
    val newSelectedDates = currentSelectedDates.toMutableMap()

    forEach { (monthYear, dates) ->
        val existingValuesForKey =
            currentSelectedDates[monthYear] ?: emptyList()
        val mergedDates =
            (existingValuesForKey + dates).toPersistentList()

        newSelectedDates[monthYear] = mergedDates
    }

    return newSelectedDates.toImmutableMap()
}

/**
 * Removes eventual duplicated dates, looking for repeated days of month in each calendar
 * month and year map values.
 *
 * Duplicated dates may occur when there is already a range selection, and the user adds a
 * new date range, starting from the last date of the current date range.
 */
private fun StaysStructuredDatesMap.removeDuplicatedDates(): StaysStructuredDatesMap {
    return mapValues { (_, monthDates) ->
        val daysOfMonth = monthDates.map { it.dayOfMonth }
        val hasDuplicatedDates = daysOfMonth.size != daysOfMonth.toSet().size

        if (!hasDuplicatedDates) return@mapValues monthDates

        val datesGroupedByDayOfMonth = monthDates.groupBy { it.dayOfMonth }

        val datesWithoutDuplications = datesGroupedByDayOfMonth.mapValues { (_, dates) ->
            dates.reduce { previous, current ->
//                val newRangeSelectionLabel =
//                    previous.rangeSelectionLabel.first to current.rangeSelectionLabel.second

                previous.copy(
                    isCheckInDate = current.isCheckInDate,
//                    rangeSelectionLabel = newRangeSelectionLabel,
                )
            }
        }

        datesWithoutDuplications.values.toPersistentList()
    }.toImmutableMap()
}
