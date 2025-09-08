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
    ): AppResultCompound<Unit, ValidationError> {
        val errors = mutableListOf<ValidationError>()

        validateDateRange(propertyPrice.startDate, propertyPrice.endDate)
            .onError { e -> errors.add(e) }

        validateAmount(propertyPrice.price).onError { e -> errors.add(e) }

        return if (errors.isEmpty()) {
            AppResultCompound.Success(Unit)
        } else AppResultCompound.Errors(errors.toList())
    }

    private fun validateDateRange(
        startDate: LocalDate?,
        endDate: LocalDate?,
    ): AppResult<Unit, ValidationError.PropertyPriceDateRange> {
        return when {
            startDate == null || endDate == null -> {
                AppResult.Error(ValidationError.PropertyPriceDateRange.IsNull)
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
