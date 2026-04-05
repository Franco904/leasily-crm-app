package com.fstengineering.leasilycrm.stayRegistration.domain.services

import com.fstengineering.leasilycrm.core.domain.helpers.AppDate
import com.fstengineering.leasilycrm.stayRegistration.domain.valueObjects.OccupiedPeriod
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toPersistentSet

class PropertyOccupiedDatesPolicy {
    companion object {
        fun getOccupiedDates(
            occupiedPeriods: ImmutableList<OccupiedPeriod>,
            stayToEditId: Long?,
        ): ImmutableSet<AppDate> {
            if (occupiedPeriods.isEmpty()) return persistentSetOf()

            val relevantPeriods = occupiedPeriods
                .sortedBy { it.checkInDateTime }
                .filter { (stayId, _, _) -> stayToEditId == null || stayId != stayToEditId }

            return relevantPeriods
                .flatMap { (_, propertyCheckInDateTime, propertyCheckOutDateTime) ->
                    val isCheckInAlsoACheckOut =
                        relevantPeriods.any { (_, _, otherCheckOutDateTime) ->
                            propertyCheckInDateTime.toDate() == otherCheckOutDateTime.toDate()
                        }

                    val isCheckOutAlsoACheckIn =
                        relevantPeriods.any { (_, otherCheckInDateTime, _) ->
                            propertyCheckOutDateTime.toDate() == otherCheckInDateTime.toDate()
                        }

                    val startDateTime =
                        if (isCheckInAlsoACheckOut) propertyCheckInDateTime
                        else propertyCheckInDateTime.plusDays(1)

                    val endDateTime =
                        if (isCheckOutAlsoACheckIn) propertyCheckOutDateTime
                        else propertyCheckOutDateTime.minusDays(1)

                    generateSequence(startDateTime) { it.plusDays(1) }
                        .takeWhile { it <= endDateTime }
                        .map { it.toDate() }
                }
                .toPersistentSet()
        }
    }
}
