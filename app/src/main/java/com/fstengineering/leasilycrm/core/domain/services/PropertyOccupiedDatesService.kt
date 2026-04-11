package com.fstengineering.leasilycrm.core.domain.services

import com.fstengineering.leasilycrm.core.domain.constants.UNAVAILABLE_PERIOD_INDEX
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDate
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppYearMonth
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyOccupiedDate
import com.fstengineering.leasilycrm.core.domain.valueObjects.PropertyOccupiedPeriod
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.collections.immutable.toPersistentMap

class PropertyOccupiedDatesService {
    companion object {
        fun getOccupiedPeriodsGroupedByMonthYear(
            occupiedPeriods: ImmutableList<PropertyOccupiedPeriod>
        ): ImmutableMap<AppYearMonth, ImmutableList<PropertyOccupiedDate>> {
            if (occupiedPeriods.isEmpty()) return persistentMapOf()

            val occupiedDatesGrouped =
                mutableMapOf<AppYearMonth, PersistentList<PropertyOccupiedDate>>()

            var previousIndex: Int? = null
            var periodIndex = 0

            val sortedOccupiedPeriods = occupiedPeriods.sortedBy { it.checkInDateTime }

            sortedOccupiedPeriods.forEachIndexed { i, occupiedPeriod ->
                val currentIndex =
                    if (occupiedPeriod.isUnavailablePeriod) UNAVAILABLE_PERIOD_INDEX else periodIndex

                val isLast = sortedOccupiedPeriods.last() == occupiedPeriod
                val next = sortedOccupiedPeriods.getOrNull(i + 1)
                val isNextAdjacentToCurrentOne = occupiedPeriod.checkOutDateTime.toDate() ==
                        next?.checkInDateTime?.toDate()

                val nextIndex = when {
                    isLast || !isNextAdjacentToCurrentOne -> null
                    next.isUnavailablePeriod -> UNAVAILABLE_PERIOD_INDEX
                    occupiedPeriod.isUnavailablePeriod -> periodIndex
                    else -> periodIndex + 1
                }

                val periodDatesGrouped = getPeriodOccupiedDatesGroupedByMonthAndYear(
                    checkInDate = occupiedPeriod.checkInDateTime.toDate(),
                    checkOutDate = occupiedPeriod.checkOutDateTime.toDate(),
                    periodId = occupiedPeriod.id,
                    tenantId = occupiedPeriod.tenantId,
                    previousIndex = previousIndex,
                    currentIndex = currentIndex,
                    nextIndex = nextIndex,
                )

                periodDatesGrouped.entries.forEach { (monthYear, dates) ->
                    val isMonthYearPresent =
                        occupiedDatesGrouped.keys.indexOf(monthYear) != -1

                    if (isMonthYearPresent) {
                        occupiedDatesGrouped[monthYear] =
                            occupiedDatesGrouped[monthYear]
                                ?.addAll(dates) ?: persistentListOf()
                    } else {
                        occupiedDatesGrouped[monthYear] = dates
                    }
                }

                previousIndex = if (isNextAdjacentToCurrentOne) currentIndex else null
                if (!occupiedPeriod.isUnavailablePeriod) periodIndex++
            }

            return occupiedDatesGrouped.toPersistentMap()
        }

        private fun getPeriodOccupiedDatesGroupedByMonthAndYear(
            checkInDate: AppDate,
            checkOutDate: AppDate,
            periodId: Long,
            tenantId: Long?,
            previousIndex: Int?,
            currentIndex: Int,
            nextIndex: Int?,
        ): ImmutableMap<AppYearMonth, PersistentList<PropertyOccupiedDate>> {
            var date = checkInDate

            val allOccupiedDates = mutableListOf<AppDate>()
            while (date <= checkOutDate) {
                allOccupiedDates.add(date)
                date = date.plusDays(1)
            }

            return allOccupiedDates
                .groupBy { date -> AppYearMonth.of(date.year, date.month) }
                .mapValues { (_, groupedDates) ->
                    groupedDates
                        .toMutableList()
                        .map { occupiedDate ->
                            val isCheckInDate = occupiedDate == checkInDate
                            val isCheckOutDate = allOccupiedDates.size > 1
                                    && occupiedDate == checkOutDate

                            PropertyOccupiedDate(
                                date = occupiedDate,
                                firstHalfPeriodIndex = if (isCheckInDate) previousIndex else currentIndex,
                                secondHalfPeriodIndex = if (isCheckOutDate) nextIndex else currentIndex,
                                periodId = periodId,
                                tenantId = tenantId,
                            )
                        }
                        .toPersistentList()
                }
                .toPersistentMap()
        }
    }
}
