package com.fstengineering.leasilycrm.core.domain.valueObjects

import com.fstengineering.leasilycrm.core.domain.helpers.AppDate
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.onError

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
            startDate: AppDate?,
            endDate: AppDate?,
        ): PropertyPricePeriod {
            validate(startDate, endDate)
                .onError { throw it.asException() }

            return PropertyPricePeriod(
                startDate = requireNotNull(startDate),
                endDate = requireNotNull(endDate),
            )
        }

        fun validate(
            startDate: AppDate?,
            endDate: AppDate?,
        ): AppResult<Unit, DomainError.Property> {
            return when {
                startDate == null || endDate == null -> AppResult.Error(DomainError.Property.PricePeriodRequired)
                startDate == endDate -> AppResult.Error(DomainError.Property.PricePeriodStartAndEndDatesAreTheSame)
                startDate > endDate -> AppResult.Error(DomainError.Property.PricePeriodStartDateIsAfterEndDate)
                else -> AppResult.Success(Unit)
            }
        }
    }
}
