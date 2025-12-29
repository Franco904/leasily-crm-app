package com.fstengineering.leasilycrm.core.domain.validators.interfaces

import com.fstengineering.leasilycrm.core.domain.models.CustomerSegment
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.DomainError

interface CustomerSegmentValidator {
    fun validate(
        customerSegment: CustomerSegment,
        isThereStoredCustomerSegmentWithSameName: Boolean,
    ): AppResultCompound<Unit, DomainError>
}
