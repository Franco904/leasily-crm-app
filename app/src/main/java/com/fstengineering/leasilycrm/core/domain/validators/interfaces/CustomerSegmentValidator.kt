package com.fstengineering.leasilycrm.core.domain.validators.interfaces

import com.fstengineering.leasilycrm.core.domain.models.CustomerSegment
import com.fstengineering.leasilycrm.core.domain.utils.AppResultCompound
import com.fstengineering.leasilycrm.core.domain.utils.ValidationError

interface CustomerSegmentValidator {
    fun validate(
        customerSegment: CustomerSegment,
    ): AppResultCompound<Unit, ValidationError>
}
