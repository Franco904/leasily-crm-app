package com.fstengineering.leasilycrm.propertyRegistration.domain.valueObjects

import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.valueObjects.AppDate
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList

class PropertyPricePeriod private constructor(
    val startDate: AppDate,
    val endDate: AppDate,
) {
    fun overlaps(other: PropertyPricePeriod): Boolean {
        return startDate < other.endDate && endDate > other.startDate
    }

    companion object {
        fun parse(
            startDate: AppDate,
            endDate: AppDate,
            otherPeriods: List<PropertyPricePeriod> = emptyList(),
        ): AppResultCompound<PropertyPricePeriod, DomainError.PropertyPricePeriod> {
            val errors = mutableListOf<DomainError.PropertyPricePeriod>()

            if (startDate == endDate) errors.add(DomainError.PropertyPricePeriod.StartAndEndDatesAreTheSame)
            if (startDate > endDate) errors.add(DomainError.PropertyPricePeriod.StartDateIsAfterEndDate)

            if (errors.isNotEmpty()) return AppResultCompound.Errors(errors.toPersistentList())

            val period = PropertyPricePeriod(startDate = startDate, endDate = endDate)

            if (otherPeriods.any { it.overlaps(period) }) {
                return AppResultCompound.Errors(persistentListOf(DomainError.PropertyPricePeriod.IsOverlappingOtherPricePeriod))
            }

            return AppResultCompound.Success(data = period)
        }
    }
}
