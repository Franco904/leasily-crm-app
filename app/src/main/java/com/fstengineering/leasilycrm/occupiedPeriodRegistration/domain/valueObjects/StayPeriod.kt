package com.fstengineering.leasilycrm.occupiedPeriodRegistration.domain.valueObjects

import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDateTime
import kotlinx.collections.immutable.toPersistentList

class StayPeriod private constructor(
    val checkInDateTime: AppDateTime,
    val checkOutDateTime: AppDateTime,
) {
    /*
        val newPeriod = StayPeriod.parse(...)

        val existingPeriods = stayRepository
            .findByPropertyId(propertyId)
            .map { it.period }

        val hasOverbooking =
            existingPeriods
                .filter { it != periodBeforeEdit }
                .any { it.overlaps(newPeriod) }

        if (hasOverbooking) {
            return AppResult.Error(DomainError.Stay.Overbooking)
        }
     */
    fun overlaps(other: StayPeriod): Boolean {
        return checkInDateTime < other.checkOutDateTime && checkOutDateTime > other.checkInDateTime
    }

    companion object {
        fun parse(
            checkInDateTime: AppDateTime,
            checkOutDateTime: AppDateTime,
        ): AppResultCompound<StayPeriod, DomainError.StayPeriod> {
            val errors = mutableListOf<DomainError.StayPeriod>()

            if (checkInDateTime.isSameDayAs(checkOutDateTime)) {
                errors.add(DomainError.StayPeriod.StartAndEndDatesAreTheSame)
            }

            if (checkInDateTime.isAfterDayOf(checkOutDateTime)) {
                errors.add(DomainError.StayPeriod.StartDateIsAfterEndDate)
            }

            if (errors.isNotEmpty()) return AppResultCompound.Errors(errors.toPersistentList())

            val parsed = StayPeriod(
                checkInDateTime = checkInDateTime,
                checkOutDateTime = checkOutDateTime,
            )

            return AppResultCompound.Success(data = parsed)
        }
    }
}
