package com.fstengineering.leasilycrm.core.domain.validators

import com.fstengineering.leasilycrm.core.domain.constants.EMAIL_REGEX
import com.fstengineering.leasilycrm.core.domain.models.PropertyOwner
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.validators.interfaces.PropertyOwnerValidator

class PropertyOwnerValidatorImpl : PropertyOwnerValidator {
    override fun validate(
        propertyOwner: PropertyOwner,
        isThereStoredPropertyOwnerWithSameName: Boolean,
    ): AppResultCompound<Unit, DomainError> {
        val errors = mutableListOf<DomainError>()

        validateName(
            name = propertyOwner.name,
            isThereStoredPropertyOwnerWithSameName = isThereStoredPropertyOwnerWithSameName,
        )
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

    private fun validateName(
        name: String?,
        isThereStoredPropertyOwnerWithSameName: Boolean,
    ): AppResult<Unit, DomainError.PropertyOwnerName> {
        return when {
            name.isNullOrBlank() -> AppResult.Error(DomainError.PropertyOwnerName.IsBlank)
            isThereStoredPropertyOwnerWithSameName -> {
                AppResult.Error(DomainError.PropertyOwnerName.BelongsToOtherPropertyOwner)
            }

            name.length > 100 -> {
                AppResult.Error(DomainError.PropertyOwnerName.LengthIsGreaterThan100Chars)
            }

            else -> AppResult.Success(Unit)
        }
    }

    private fun validatePhone(phone: String): AppResult<Unit, DomainError.PropertyOwnerPhone> {
        return when {
            phone.length > 20 -> {
                AppResult.Error(DomainError.PropertyOwnerPhone.LengthIsGreaterThan20Chars)
            }

            else -> AppResult.Success(Unit)
        }
    }

    private fun validateEmail(email: String): AppResult<Unit, DomainError.PropertyOwnerEmail> {
        return when {
            !email.trim().matches(Regex(EMAIL_REGEX, RegexOption.IGNORE_CASE)) -> {
                AppResult.Error(DomainError.PropertyOwnerEmail.InvalidFormat)
            }

            email.length > 100 -> {
                AppResult.Error(DomainError.PropertyOwnerEmail.LengthIsGreaterThan120Chars)
            }

            else -> AppResult.Success(Unit)
        }
    }

    private fun validatePaymentKey(paymentKey: String): AppResult<Unit, DomainError.PropertyOwnerPaymentKey> {
        return when {
            paymentKey.length > 120 -> {
                AppResult.Error(DomainError.PropertyOwnerPaymentKey.LengthIsGreaterThan120Chars)
            }

            else -> AppResult.Success(Unit)
        }
    }
}
