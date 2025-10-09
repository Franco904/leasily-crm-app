package com.fstengineering.leasilycrm.core.domain.validators

import com.fstengineering.leasilycrm.core.domain.constants.EMAIL_REGEX
import com.fstengineering.leasilycrm.core.domain.models.Tenant
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.ValidationError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.validators.interfaces.TenantValidator

class TenantValidatorImpl : TenantValidator {
    override fun validate(
        tenant: Tenant,
        isThereStoredTenantWithSameName: Boolean,
    ): AppResultCompound<Unit, ValidationError> {
        val errors = mutableListOf<ValidationError>()

        validateName(
            name = tenant.name,
            isThereStoredTenantWithSameName = isThereStoredTenantWithSameName,
        )
            .onError { e -> errors.add(e) }

        if (tenant.phone != null) {
            validatePhone(phone = tenant.phone)
                .onError { e -> errors.add(e) }
        }

        if (tenant.email != null) {
            validateEmail(email = tenant.email)
                .onError { e -> errors.add(e) }
        }

        return if (errors.isEmpty()) {
            AppResultCompound.Success(Unit)
        } else AppResultCompound.Errors(errors.toList())
    }

    private fun validateName(
        name: String?,
        isThereStoredTenantWithSameName: Boolean,
    ): AppResult<Unit, ValidationError.TenantName> {
        return when {
            name.isNullOrBlank() -> AppResult.Error(ValidationError.TenantName.IsBlank)
            isThereStoredTenantWithSameName -> {
                AppResult.Error(ValidationError.TenantName.BelongsToOtherTenant)
            }

            name.length > 100 -> {
                AppResult.Error(ValidationError.TenantName.LengthIsGreaterThan100Chars)
            }

            else -> AppResult.Success(Unit)
        }
    }

    private fun validatePhone(phone: String): AppResult<Unit, ValidationError.TenantPhone> {
        return when {
            phone.length > 20 -> {
                AppResult.Error(ValidationError.TenantPhone.LengthIsGreaterThan20Chars)
            }

            else -> AppResult.Success(Unit)
        }
    }

    private fun validateEmail(email: String): AppResult<Unit, ValidationError.TenantEmail> {
        return when {
            !email.trim().matches(Regex(EMAIL_REGEX, RegexOption.IGNORE_CASE)) -> {
                AppResult.Error(ValidationError.TenantEmail.InvalidFormat)
            }

            email.length > 100 -> {
                AppResult.Error(ValidationError.TenantEmail.LengthIsGreaterThan120Chars)
            }

            else -> AppResult.Success(Unit)
        }
    }
}
