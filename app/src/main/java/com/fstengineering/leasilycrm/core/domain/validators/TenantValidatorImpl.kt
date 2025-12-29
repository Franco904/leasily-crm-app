package com.fstengineering.leasilycrm.core.domain.validators

import com.fstengineering.leasilycrm.core.domain.constants.EMAIL_REGEX
import com.fstengineering.leasilycrm.core.domain.models.Tenant
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.validators.interfaces.TenantValidator

class TenantValidatorImpl : TenantValidator {
    override fun validate(
        tenant: Tenant,
        isThereStoredTenantWithSameName: Boolean,
    ): AppResultCompound<Unit, DomainError> {
        val errors = mutableListOf<DomainError>()

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
    ): AppResult<Unit, DomainError.TenantName> {
        return when {
            name.isNullOrBlank() -> AppResult.Error(DomainError.TenantName.IsBlank)
            isThereStoredTenantWithSameName -> {
                AppResult.Error(DomainError.TenantName.BelongsToOtherTenant)
            }

            name.length > 100 -> {
                AppResult.Error(DomainError.TenantName.LengthIsGreaterThan100Chars)
            }

            else -> AppResult.Success(Unit)
        }
    }

    private fun validatePhone(phone: String): AppResult<Unit, DomainError.TenantPhone> {
        return when {
            phone.length > 20 -> {
                AppResult.Error(DomainError.TenantPhone.LengthIsGreaterThan20Chars)
            }

            else -> AppResult.Success(Unit)
        }
    }

    private fun validateEmail(email: String): AppResult<Unit, DomainError.TenantEmail> {
        return when {
            !email.trim().matches(Regex(EMAIL_REGEX, RegexOption.IGNORE_CASE)) -> {
                AppResult.Error(DomainError.TenantEmail.InvalidFormat)
            }

            email.length > 100 -> {
                AppResult.Error(DomainError.TenantEmail.LengthIsGreaterThan120Chars)
            }

            else -> AppResult.Success(Unit)
        }
    }
}
