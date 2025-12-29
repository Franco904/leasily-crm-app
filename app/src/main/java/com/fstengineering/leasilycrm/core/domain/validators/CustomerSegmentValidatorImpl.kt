package com.fstengineering.leasilycrm.core.domain.validators

import com.fstengineering.leasilycrm.core.domain.constants.TENANT_CUSTOMER_SEGMENT_TYPE_ID
import com.fstengineering.leasilycrm.core.domain.models.CustomerSegment
import com.fstengineering.leasilycrm.core.domain.models.CustomerSegmentType
import com.fstengineering.leasilycrm.core.domain.utils.AppResult
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.DomainError
import com.fstengineering.leasilycrm.core.domain.utils.onError
import com.fstengineering.leasilycrm.core.domain.validators.interfaces.CustomerSegmentValidator

class CustomerSegmentValidatorImpl : CustomerSegmentValidator {
    override fun validate(
        customerSegment: CustomerSegment,
        isThereStoredCustomerSegmentWithSameName: Boolean,
    ): AppResultCompound<Unit, DomainError> {
        val errors = mutableListOf<DomainError>()

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
    ): AppResult<Unit, DomainError.CustomerSegmentName> {
        return when {
            name.isNullOrBlank() -> AppResult.Error(DomainError.CustomerSegmentName.IsBlank)
            type != null && isThereStoredCustomerSegmentWithSameName -> {
                if (type.id == TENANT_CUSTOMER_SEGMENT_TYPE_ID) {
                    AppResult.Error(DomainError.CustomerSegmentName.BelongsToOtherTenantSegment)
                } else {
                    AppResult.Error(DomainError.CustomerSegmentName.BelongsToOtherPropertyOwnerSegment)
                }
            }

            name.length > 75 -> AppResult.Error(DomainError.CustomerSegmentName.LengthIsGreaterThan75Chars)
            else -> AppResult.Success(Unit)
        }
    }

    private fun validateType(type: CustomerSegmentType?): AppResult<Unit, DomainError.CustomerSegmentType> {
        return when {
            type == null || type.name == null -> AppResult.Error(DomainError.CustomerSegmentType.IsNull)
            else -> AppResult.Success(Unit)
        }
    }
}
