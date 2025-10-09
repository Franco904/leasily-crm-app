package com.fstengineering.leasilycrm.core.domain.validators

import com.fstengineering.leasilycrm.core.domain.constants.TENANT_CUSTOMER_SEGMENT_TYPE_ID
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
        isThereStoredCustomerSegmentWithSameName: Boolean,
    ): AppResultCompound<Unit, ValidationError> {
        val errors = mutableListOf<ValidationError>()

        validateName(
            name = customerSegment.name,
            type = customerSegment.type,
            isThereStoredCustomerSegmentWithSameName = isThereStoredCustomerSegmentWithSameName,
        ).onError { e -> errors.add(e) }

        validateType(type = customerSegment.type).onError { e -> errors.add(e) }

        return if (errors.isEmpty()) {
            AppResultCompound.Success(Unit)
        } else AppResultCompound.Errors(errors.toList())
    }

    private fun validateName(
        name: String?,
        type: CustomerSegmentType?,
        isThereStoredCustomerSegmentWithSameName: Boolean,
    ): AppResult<Unit, ValidationError.CustomerSegmentName> {
        return when {
            name.isNullOrBlank() -> AppResult.Error(ValidationError.CustomerSegmentName.IsBlank)
            type != null && isThereStoredCustomerSegmentWithSameName -> {
                if (type.id == TENANT_CUSTOMER_SEGMENT_TYPE_ID) {
                    AppResult.Error(ValidationError.CustomerSegmentName.BelongsToOtherTenantSegment)
                } else {
                    AppResult.Error(ValidationError.CustomerSegmentName.BelongsToOtherPropertyOwnerSegment)
                }
            }

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
