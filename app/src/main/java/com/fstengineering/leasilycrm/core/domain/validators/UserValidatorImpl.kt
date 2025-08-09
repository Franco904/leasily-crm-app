package com.fstengineering.leasilycrm.core.domain.validators

import com.fstengineering.leasilycrm.core.domain.models.User
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.ValidationError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.validators.interfaces.UserValidator

class UserValidatorImpl : UserValidator {
    override fun validate(user: User): AppResultCompound<Unit, ValidationError> {
        val errors = mutableListOf<ValidationError>()

        validateName(user.name).onError { e -> errors.add(e) }

        if (user.creciNumber != null) {
            validateCreciNumber(user.creciNumber).onError { e -> errors.add(e) }
        }

        return if (errors.isEmpty()) {
            AppResultCompound.Success(Unit)
        } else AppResultCompound.Errors(errors.toList())
    }

    private fun validateName(name: String?): AppResult<Unit, ValidationError.UserName> {
        return when {
            name.isNullOrBlank() -> AppResult.Error(ValidationError.UserName.IsBlank)
            name.length > 75 -> AppResult.Error(ValidationError.UserName.LengthIsGreaterThan75Chars)
            else -> AppResult.Success(Unit)
        }
    }

    private fun validateCreciNumber(creciNumber: String): AppResult<Unit, ValidationError.CreciNumber> {
        return when {
            creciNumber.length > 8 -> AppResult.Error(ValidationError.CreciNumber.LengthIsGreaterThan8Chars)
            else -> AppResult.Success(Unit)
        }
    }
}
