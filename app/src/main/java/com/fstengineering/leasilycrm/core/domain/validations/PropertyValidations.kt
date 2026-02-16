package com.fstengineering.leasilycrm.core.domain.validations

import com.fstengineering.leasilycrm.core.domain.helpers.AppDate
import com.fstengineering.leasilycrm.core.domain.helpers.AppDecimal
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.ParsingError

fun validatePropertyOwner(ownerId: Long?): AppResult<Unit, ParsingError.Property> {
    return when {
        ownerId == null -> AppResult.Error(ParsingError.Property.OwnerRequired)
        else -> AppResult.Success(Unit)
    }
}

fun validatePropertyType(typeId: Long?): AppResult<Unit, ParsingError.Property> {
    return when {
        typeId == null -> AppResult.Error(ParsingError.Property.TypeRequired)
        else -> AppResult.Success(Unit)
    }
}

fun validatePropertyNeighborhood(neighborhood: String?): AppResult<Unit, ParsingError.Property> {
    return when {
        neighborhood.isNullOrBlank() -> AppResult.Error(ParsingError.Property.NeighborhoodRequired)
        else -> AppResult.Success(Unit)
    }
}

fun validatePropertyStreetAddress(streetAddress: String?): AppResult<Unit, ParsingError.Property> {
    return when {
        streetAddress.isNullOrBlank() -> AppResult.Error(ParsingError.Property.StreetAddressRequired)
        else -> AppResult.Success(Unit)
    }
}

fun validatePropertyPricePeriod(
    startDate: AppDate?,
    endDate: AppDate?,
): AppResult<Unit, ParsingError.Property> {
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
        startDate == null || endDate == null -> AppResult.Error(ParsingError.Property.PricePeriodRequired)
        startDate == endDate -> AppResult.Error(ParsingError.Property.PricePeriodStartAndEndDatesAreTheSame)
        startDate > endDate -> AppResult.Error(ParsingError.Property.PricePeriodStartDateIsAfterEndDate)
        else -> AppResult.Success(Unit)
    }
}

fun validatePropertyPriceAmount(
    amount: AppDecimal?,
): AppResult<Unit, ParsingError.Property> {
    return when {
        amount == null -> AppResult.Error(ParsingError.Property.PriceAmountRequired)
        amount.isNegative -> AppResult.Error(ParsingError.Property.PriceAmountIsNegative)
        else -> AppResult.Success(Unit)
    }
}
