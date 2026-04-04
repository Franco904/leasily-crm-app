package com.fstengineering.leasilycrm.propertyRegistration.domain.valueObjects

import com.fstengineering.leasilycrm.core.domain.helpers.AppDate
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import kotlinx.collections.immutable.toPersistentList

class PropertyPricePeriod private constructor(
    val startDate: AppDate,
    val endDate: AppDate,
) {
    /*
        val newPeriod = PropertyPricePeriod.parse(...)

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
    fun overlaps(other: PropertyPricePeriod): Boolean {
        return startDate < other.endDate && endDate > other.startDate
    }

    companion object {
        fun parse(
            startDate: AppDate,
            endDate: AppDate,
        ): AppResultCompound<PropertyPricePeriod, DomainError.PropertyPricePeriod> {
            val errors = mutableListOf<DomainError.PropertyPricePeriod>()

            if (startDate == endDate) errors.add(DomainError.PropertyPricePeriod.StartAndEndDatesAreTheSame)
            if (startDate > endDate) errors.add(DomainError.PropertyPricePeriod.StartDateIsAfterEndDate)

            if (errors.isNotEmpty()) return AppResultCompound.Errors(errors.toPersistentList())

            val parsed = PropertyPricePeriod(
                startDate = startDate,
                endDate = endDate,
            )

            return AppResultCompound.Success(data = parsed)
        }
    }
}
