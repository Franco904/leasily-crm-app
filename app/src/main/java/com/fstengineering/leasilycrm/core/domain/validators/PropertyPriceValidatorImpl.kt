package com.fstengineering.leasilycrm.core.domain.validators

import com.fstengineering.leasilycrm.core.domain.models.PropertyPrice
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.ValidationError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.validators.interfaces.PropertyPriceValidator
import java.math.BigDecimal
import java.time.LocalDate

class PropertyPriceValidatorImpl : PropertyPriceValidator {
    override fun validate(
        propertyPrice: PropertyPrice,
        propertyPriceBeforeEdit: PropertyPrice?,
        otherPropertyPrices: List<PropertyPrice>,
    ): AppResultCompound<Unit, ValidationError> {
        val errors = mutableListOf<ValidationError>()

        validatePeriod(
            startDate = propertyPrice.startDate,
            endDate = propertyPrice.endDate,
            startDateBeforeEdit = propertyPriceBeforeEdit?.startDate,
            otherPropertyPricesStartDates = otherPropertyPrices.mapNotNull { it.startDate },
        )
            .onError { e -> errors.add(e) }

        validateAmount(propertyPrice.price).onError { e -> errors.add(e) }

        return if (errors.isEmpty()) {
            AppResultCompound.Success(Unit)
        } else AppResultCompound.Errors(errors.toList())
    }

    private fun validatePeriod(
        startDate: LocalDate?,
        endDate: LocalDate?,
        startDateBeforeEdit: LocalDate?,
        otherPropertyPricesStartDates: List<LocalDate>,
    ): AppResult<Unit, ValidationError.PropertyPricePeriod> {
        return when {
            startDate == null || endDate == null -> {
                AppResult.Error(ValidationError.PropertyPricePeriod.IsNull)
            }

            startDate == endDate -> {
                AppResult.Error(ValidationError.PropertyPricePeriod.StartAndEndDaysAreTheSame)
            }

            otherPropertyPricesStartDates.isNotEmpty() -> {
                val isThereAnyPropertyPriceOverlapping = otherPropertyPricesStartDates
                    .filter { it != startDateBeforeEdit }
                    .any { existingPriceStartDate ->
                        startDate < existingPriceStartDate && endDate > existingPriceStartDate
                    }

                if (!isThereAnyPropertyPriceOverlapping) return AppResult.Success(Unit)

                return AppResult.Error(ValidationError.PropertyPricePeriod.IsOverlappingOtherPropertyPricePeriod)
            }

            else -> AppResult.Success(Unit)
        }
    }

    private fun validateAmount(
        amount: BigDecimal?,
    ): AppResult<Unit, ValidationError.PropertyPriceAmount> {
        return when {
            amount == null -> {
                AppResult.Error(ValidationError.PropertyPriceAmount.IsNull)
            }

            else -> AppResult.Success(Unit)
        }
    }
}
