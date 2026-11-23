package com.fstengineering.leasilycrm.occupiedPeriodRegistration.domain.services

import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDate
import com.fstengineering.leasilycrm.occupiedPeriodRegistration.application.models.dtos.OccupiedPeriodReadModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toPersistentSet

object ImovelOccupiedDatesService {
    fun getOccupiedDates(
        occupiedPeriods: ImmutableList<OccupiedPeriodReadModel>,
        stayToEditId: Long?,
    ): ImmutableSet<AppDate> {
        if (occupiedPeriods.isEmpty()) return persistentSetOf()

        val relevantPeriods = occupiedPeriods
            .sortedBy { it.checkInDateTime }
            .filter { (stayId, _, _) -> stayToEditId == null || stayId != stayToEditId }

        return relevantPeriods
            .flatMap { (_, imovelCheckInDateTime, imovelCheckOutDateTime) ->
                val isCheckInAlsoACheckOut =
                    relevantPeriods.any { (_, _, otherCheckOutDateTime) ->
                        imovelCheckInDateTime.toDate() == otherCheckOutDateTime.toDate()
                    }

                val isCheckOutAlsoACheckIn =
                    relevantPeriods.any { (_, otherCheckInDateTime, _) ->
                        imovelCheckOutDateTime.toDate() == otherCheckInDateTime.toDate()
                    }

                val startDateTime =
                    if (isCheckInAlsoACheckOut) imovelCheckInDateTime
                    else imovelCheckInDateTime.plusDays(1)

                val endDateTime =
                    if (isCheckOutAlsoACheckIn) imovelCheckOutDateTime
                    else imovelCheckOutDateTime.minusDays(1)

                generateSequence(startDateTime) { it.plusDays(1) }
                    .takeWhile { it <= endDateTime }
                    .map { it.toDate() }
            }
            .toPersistentSet()
    }
}
