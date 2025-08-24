package com.fstengineering.leasilycrm.core.domain.validators

import com.fstengineering.leasilycrm.core.domain.models.CustomerSegment
import com.fstengineering.leasilycrm.core.domain.models.CustomerSegmentType
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.ValidationError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.validators.interfaces.CustomerSegmentValidator

class CustomerSegmentValidatorImpl : CustomerSegmentValidator {
    override fun validate(
        customerSegment: CustomerSegment,
    ): AppResultCompound<Unit, ValidationError> {
        val errors = mutableListOf<ValidationError>()

        validateName(name = customerSegment.name).onError { e -> errors.add(e) }
        validateType(type = customerSegment.type).onError { e -> errors.add(e) }

        return if (errors.isEmpty()) {
            AppResultCompound.Success(Unit)
        } else AppResultCompound.Errors(errors.toList())
    }

    private fun validateName(name: String?): AppResult<Unit, ValidationError.CustomerSegmentName> {
        return when {
            name.isNullOrBlank() -> AppResult.Error(ValidationError.CustomerSegmentName.IsBlank)
            name.length > 75 -> AppResult.Error(ValidationError.CustomerSegmentName.LengthIsGreaterThan75Chars)
            else -> AppResult.Success(Unit)
        }
    }

    private fun validateType(type: CustomerSegmentType?): AppResult<Unit, ValidationError.CustomerSegmentType> {
        return when {
            type == null || type.name == null -> AppResult.Error(ValidationError.CustomerSegmentType.IsNull)
            else -> AppResult.Success(Unit)
        }
    }
}
