package com.fstengineering.leasilycrm.core.domain.valueObjects

import com.fstengineering.leasilycrm.core.domain.helpers.AppDate
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.onError

class PropertyPricePeriod private constructor(
    val startDate: AppDate,
    val endDate: AppDate,
) {
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
            // otherPropertyPricesStartDates.isNotEmpty() -> {
            //                val isThereAnyPropertyPriceOverlapping = otherPropertyPricesStartDates
            //                    .filter { it != startDateBeforeEdit }
            //                    .any { existingPriceStartDate ->
            //                        startDate < existingPriceStartDate && endDate > existingPriceStartDate
            //                    }
            //
            //                if (!isThereAnyPropertyPriceOverlapping) return AppResult.Success(Unit)
            //
            //                return AppResult.Error(ParsingError.PropertyPricePeriod.IsOverlappingOtherPropertyPricePeriod)
            //            }

            return when {
                startDate == null || endDate == null -> AppResult.Error(DomainError.Property.PricePeriodRequired)
                startDate == endDate -> AppResult.Error(DomainError.Property.PricePeriodStartAndEndDatesAreTheSame)
                startDate > endDate -> AppResult.Error(DomainError.Property.PricePeriodStartDateIsAfterEndDate)
                else -> AppResult.Success(Unit)
            }
        }
    }
}
