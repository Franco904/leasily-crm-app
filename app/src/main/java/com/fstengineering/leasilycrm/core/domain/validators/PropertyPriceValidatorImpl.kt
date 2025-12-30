package com.fstengineering.leasilycrm.core.domain.validators

import com.fstengineering.leasilycrm.core.domain.models.PropertyPrice
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.validators.interfaces.PropertyPriceValidator
import java.math.BigDecimal
import java.time.LocalDate

class PropertyPriceValidatorImpl : PropertyPriceValidator {
    override fun validate(
        propertyPrice: PropertyPrice,
        propertyPriceBeforeEdit: PropertyPrice?,
        otherPropertyPrices: List<PropertyPrice>,
    ): AppResultCompound<Unit, DomainError> {
        val errors = mutableListOf<DomainError>()

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
    ): AppResult<Unit, DomainError.PropertyPricePeriod> {
        return when {
            startDate == null || endDate == null -> {
                AppResult.Error(DomainError.PropertyPricePeriod.IsNull)
            }

            startDate == endDate -> {
                AppResult.Error(DomainError.PropertyPricePeriod.StartAndEndDaysAreTheSame)
            }

            otherPropertyPricesStartDates.isNotEmpty() -> {
                val isThereAnyPropertyPriceOverlapping = otherPropertyPricesStartDates
                    .filter { it != startDateBeforeEdit }
                    .any { existingPriceStartDate ->
                        startDate < existingPriceStartDate && endDate > existingPriceStartDate
                    }

                if (!isThereAnyPropertyPriceOverlapping) return AppResult.Success(Unit)

                return AppResult.Error(DomainError.PropertyPricePeriod.IsOverlappingOtherPropertyPricePeriod)
            }

            else -> AppResult.Success(Unit)
        }
    }

    private fun validateAmount(
        amount: BigDecimal?,
    ): AppResult<Unit, DomainError.PropertyPriceAmount> {
        return when {
            amount == null -> {
                AppResult.Error(DomainError.PropertyPriceAmount.IsNull)
            }

            else -> AppResult.Success(Unit)
        }
    }
}
