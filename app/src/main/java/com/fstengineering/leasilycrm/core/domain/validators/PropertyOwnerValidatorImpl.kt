package com.fstengineering.leasilycrm.core.domain.validators

import com.fstengineering.leasilycrm.core.domain.constants.EMAIL_REGEX
import com.fstengineering.leasilycrm.core.domain.models.PropertyOwner
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.ValidationError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.validators.interfaces.PropertyOwnerValidator

class PropertyOwnerValidatorImpl : PropertyOwnerValidator {
    override fun validate(
        propertyOwner: PropertyOwner,
    ): AppResultCompound<Unit, ValidationError> {
        val errors = mutableListOf<ValidationError>()

        validateName(name = propertyOwner.name)
            .onError { e -> errors.add(e) }

        if (propertyOwner.phone != null) {
            validatePhone(phone = propertyOwner.phone)
                .onError { e -> errors.add(e) }
        }

        if (propertyOwner.email != null) {
            validateEmail(email = propertyOwner.email)
                .onError { e -> errors.add(e) }
        }

        if (propertyOwner.paymentKey != null) {
            validatePaymentKey(paymentKey = propertyOwner.paymentKey)
                .onError { e -> errors.add(e) }
        }

        return if (errors.isEmpty()) {
            AppResultCompound.Success(Unit)
        } else AppResultCompound.Errors(errors.toList())
    }

    private fun validateName(name: String?): AppResult<Unit, ValidationError.PropertyOwnerName> {
        return when {
            name.isNullOrBlank() -> AppResult.Error(ValidationError.PropertyOwnerName.IsBlank)
            name.length > 100 -> {
                AppResult.Error(ValidationError.PropertyOwnerName.LengthIsGreaterThan100Chars)
            }

            else -> AppResult.Success(Unit)
        }
    }

    private fun validatePhone(phone: String): AppResult<Unit, ValidationError.PropertyOwnerPhone> {
        return when {
            phone.length > 20 -> {
                AppResult.Error(ValidationError.PropertyOwnerPhone.LengthIsGreaterThan20Chars)
            }

            else -> AppResult.Success(Unit)
        }
    }

    private fun validateEmail(email: String): AppResult<Unit, ValidationError.PropertyOwnerEmail> {
        return when {
            email.trim().matches(Regex(EMAIL_REGEX, RegexOption.IGNORE_CASE)) -> {
                AppResult.Error(ValidationError.PropertyOwnerEmail.InvalidFormat)
            }

            email.length > 100 -> {
                AppResult.Error(ValidationError.PropertyOwnerEmail.LengthIsGreaterThan120Chars)
            }

            else -> AppResult.Success(Unit)
        }
    }

    private fun validatePaymentKey(paymentKey: String): AppResult<Unit, ValidationError.PropertyOwnerPaymentKey> {
        return when {
            paymentKey.length > 120 -> {
                AppResult.Error(ValidationError.PropertyOwnerPaymentKey.LengthIsGreaterThan120Chars)
            }

            else -> AppResult.Success(Unit)
        }
    }
}
